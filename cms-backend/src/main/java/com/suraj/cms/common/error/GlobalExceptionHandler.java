package com.suraj.cms.common.error;


import com.suraj.cms.common.error.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Centralizes error shaping & status mapping.
 * <p>
 * All REST errors return Spring ProblemDetail (RFC 9457) with extensions:
 * - code (ErrorCode)
 * - traceId (from MDC, for support/debug correlation)
 * - metadata (optional, structured)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Domain-level API exceptions with explicit status and code
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handleApi(ApiException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getHttpStatus());
        ProblemDetail pd = base(status, status.getReasonPhrase(), ex.getMessage(), req);
        return ResponseEntity.status(status).body(attach(pd, ex.getCode(), ex.getMetadata()));
    }

    /**
     * Method not allowed (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        ProblemDetail pd = base(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", ex.getMessage(), req);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(attach(pd, ErrorCode.METHOD_NOT_ALLOWED, Map.of()));
    }


    /**
     * No handler matched (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = base(HttpStatus.NOT_FOUND, "Not found", "Resource not found.", req);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(attach(pd, ErrorCode.NOT_FOUND, Map.of("path", ex.getRequestURL())));
    }


    /**
     * Data integrity → 409 (e.g., unique constraint violation)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        ProblemDetail pd = base(HttpStatus.CONFLICT, "Resource conflict", "Operation conflicts with existing data.", req);
        String cause = Optional.ofNullable(ex.getMostSpecificCause()).map(Throwable::getMessage).orElse("n/a");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(attach(pd, ErrorCode.RESOURCE_CONFLICT, Map.of("cause", cause)));
    }

    /**
     * Bean validation (JSR 380) on @Valid annotated request DTOs
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> metadata = new LinkedHashMap<>();
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(fe -> Map.of("field", Optional.ofNullable(fe.getField()).orElse(""), "message", Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid"))).collect(Collectors.toList());
        if (!fieldErrors.isEmpty()) {
            metadata.put("fields", fieldErrors);
        }
        ProblemDetail pd = base(status, "Validation failed", "One or more fields are invalid.", req);
        return ResponseEntity.status(status).body(attach(pd, ErrorCode.VALIDATION_FAILED, metadata));
    }

    /**
     * Constraint violations outside of request bodies (e.g., path params)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<Map<String, String>> violations = ex.getConstraintViolations().stream().map(v -> Map.of("property", Optional.ofNullable(v.getPropertyPath()).map(Object::toString).orElse(""), "message", Optional.ofNullable(v.getMessage()).orElse("invalid"))).collect(Collectors.toList());
        Map<String, Object> metadata = violations.isEmpty() ? Map.of() : Map.of("violations", violations);
        ProblemDetail pd = base(status, "Validation failed", "Request parameters are invalid.", req);
        return ResponseEntity.status(status).body(attach(pd, ErrorCode.VALIDATION_FAILED, metadata));
    }

    /**
     * Malformed JSON/XML request bodies
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail pd = base(status, "Malformed request", "Request payload is malformed or unreadable.", req);
        return ResponseEntity.status(status).body(attach(pd, ErrorCode.MALFORMED_REQUEST, Map.of()));
    }


// ---- Security fallbacks (framework may short-circuit via our custom handlers) --------------


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        ProblemDetail pd = base(HttpStatus.UNAUTHORIZED, "Authentication failed", "Authentication is required or has failed.", req);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(attach(pd, ErrorCode.AUTHENTICATION_FAILED, Map.of()));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        ProblemDetail pd = base(HttpStatus.FORBIDDEN, "Access denied", "You do not have permission to perform this action.", req);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(attach(pd, ErrorCode.ACCESS_DENIED, Map.of()));
    }


// ---- Fallback 5xx -------------------------------------------------------------------------


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandled(Exception ex, HttpServletRequest req) {
// Intentionally do not leak stack traces/details to clients; include traceId for support.
        ProblemDetail pd = base(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Something went wrong. Contact support with the provided traceId.", req);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(attach(pd, ErrorCode.INTERNAL_ERROR, Map.of()));
    }

    private ProblemDetail base(HttpStatus status, String title, String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setInstance(URI.create(req.getRequestURI()));
        return pd;
    }

    /**
     * Attach common extensions: type URI, code, traceId, metadata
     */
    private ProblemDetail attach(ProblemDetail pd, ErrorCode code, Map<String, Object> metadata) {
        // Attach a type based on the code for easier categorization (RFC 9457 extension)
        try {
            pd.setType(URI.create("urn:error:" + code.name()));
        } catch (Exception ignored) { /* ignore invalid URI issues */ }

        pd.setProperty("code", code.name());

        String traceId = Optional.ofNullable(MDC.get("traceId")).orElse(Optional.ofNullable(MDC.get("X-B3-TraceId")).orElse(null));
        if (traceId != null && !traceId.isBlank()) {
            pd.setProperty("traceId", traceId);
        }

        if (metadata != null && !metadata.isEmpty()) {
            pd.setProperty("metadata", metadata);
        }
        return pd;
    }
}

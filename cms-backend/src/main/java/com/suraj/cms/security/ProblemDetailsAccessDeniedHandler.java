package com.suraj.cms.security;


import com.suraj.cms.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URI;


@Component
/**
 * AccessDeniedHandler that returns an RFC 7807 ProblemDetail (403) when an
 * authenticated user lacks required authority. Populates title/detail, type
 * using ACCESS_DENIED, instance from the request URI, and an optional traceId,
 * then writes application/problem+json to the response.
 */
public class ProblemDetailsAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException ex) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Access denied");
        pd.setDetail("You do not have permission to perform this action.");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setType(URI.create("https://errors.cms.suraj.app/" + ErrorCode.ACCESS_DENIED.name().toLowerCase()));
        pd.setProperty("code", ErrorCode.ACCESS_DENIED.name());
        String traceId = MDC.get("traceId");
        if (traceId != null) pd.setProperty("traceId", traceId);


        resp.setStatus(HttpStatus.FORBIDDEN.value());
        resp.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        resp.getWriter().write(pd.toString());
    }
}

package com.suraj.cms.security;


import com.suraj.cms.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URI;
@Component
/**
 * AuthenticationEntryPoint that returns an RFC 7807 ProblemDetail (401) for
 * unauthenticated requests to protected resources. Populates title/detail,
 * type using AUTHENTICATION_FAILED, instance from the request URI, and an
 * optional traceId, then writes application/problem+json to the response.
 */
public class ProblemDetailsAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException ex) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Authentication failed");
        pd.setDetail("Authentication is required or has failed.");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setType(URI.create("https://errors.cms.suraj.app/" + ErrorCode.AUTHENTICATION_FAILED.name().toLowerCase()));
        pd.setProperty("code", ErrorCode.AUTHENTICATION_FAILED.name());
        String traceId = MDC.get("traceId");
        if (traceId != null) pd.setProperty("traceId", traceId);


        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        resp.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
// If your Spring version doesn't serialize ProblemDetail via toString(), use Jackson ObjectMapper instead.
        resp.getWriter().write(pd.toString());
    }
}

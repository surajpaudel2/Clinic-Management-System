package com.suraj.cms.common.web;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Generates/propagates a per-request traceId so you can correlate logs and responses.
 * - Reads X-Request-Id if present (propagation from an API Gateway / client)
 * - Otherwise generates a short UUID
 * - Stores in MDC so your log pattern can include it
 * - Emits X-Trace-Id response header for clients/support
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements Filter {
    public static final String TRACE_ID = "traceId";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String incoming = req.getHeader("X-Request-Id");
        String traceId = Optional.ofNullable(incoming)
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString().replace("-", "").substring(0, 16));

        MDC.put(TRACE_ID, traceId);
        try {
            ((HttpServletResponse) response).setHeader("X-Trace-Id", traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}
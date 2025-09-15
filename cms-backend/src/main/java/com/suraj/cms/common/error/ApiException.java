package com.suraj.cms.common.error;

import lombok.Getter;

import java.util.Map;


/**
 * Base exception for domain/infrastructure errors.
 *
 * - Keeps boundary with controllers clean
 * - Gives us a single place to attach metadata (e.g., entity ids)
 * - Avoids leaking internal details to clients
 */
@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode code;
    private final int httpStatus;
    private final Map<String, Object> metadata; // optional structured context for clients/support


    public ApiException(ErrorCode code, int httpStatus, String message) {
        this(code, httpStatus, message, Map.of());
    }


    public ApiException(ErrorCode code, int httpStatus, String message, Map<String, Object> metadata) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.metadata = metadata;
    }
}
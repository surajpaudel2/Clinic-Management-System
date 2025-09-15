package com.suraj.cms.common.error.exceptions;

import com.suraj.cms.common.error.ApiException;
import com.suraj.cms.common.error.ErrorCode;

/**
 * Validation failed (400) for custom flows (beyond Bean Validation)
 */
public class ValidationFailedException extends ApiException {
    public ValidationFailedException(String message) {
        super(ErrorCode.VALIDATION_FAILED, 400, message);
    }
}

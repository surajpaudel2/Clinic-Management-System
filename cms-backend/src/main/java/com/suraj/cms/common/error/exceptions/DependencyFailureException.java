package com.suraj.cms.common.error.exceptions;


import com.suraj.cms.common.error.ApiException;
import com.suraj.cms.common.error.ErrorCode;

/**
 * Upstream dependency error (502)
 */
public class DependencyFailureException extends ApiException {
    public DependencyFailureException(String message) {
        super(ErrorCode.DEPENDENCY_FAILURE, 502, message);
    }
}

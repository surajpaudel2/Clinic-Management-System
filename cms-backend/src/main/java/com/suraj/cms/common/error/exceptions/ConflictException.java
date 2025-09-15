package com.suraj.cms.common.error.exceptions;

import com.suraj.cms.common.error.ApiException;
import com.suraj.cms.common.error.ErrorCode;

/** Conflict (409), e.g., unique key violation, duplicate resource */
public class ConflictException extends ApiException {
    public ConflictException(String message) { super(ErrorCode.RESOURCE_CONFLICT, 409, message); }
}
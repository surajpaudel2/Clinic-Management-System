package com.suraj.cms.common.error.exceptions;


import com.suraj.cms.common.error.ApiException;
import com.suraj.cms.common.error.ErrorCode;


/** Domain-level Not Found (404) */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) { super(ErrorCode.NOT_FOUND, 404, message); }
}
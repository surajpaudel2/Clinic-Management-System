package com.suraj.cms.common.error.exceptions;

import com.suraj.cms.common.error.ApiException;
import com.suraj.cms.common.error.ErrorCode;

/** Rate limited (429) — throw from your limiter */
public class RateLimitedException extends ApiException {
    public RateLimitedException(String message) { super(ErrorCode.RATE_LIMITED, 429, message); }
}
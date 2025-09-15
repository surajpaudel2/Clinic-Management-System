package com.suraj.cms.common.error;

/**
 * Central list of application error codes.
 * Keep codes stable (clients and dashboards may depend on them).
 */
public enum ErrorCode {
    // AuthN/AuthZ
    AUTHENTICATION_FAILED,
    ACCESS_DENIED,


    // Generic client-side problems
    VALIDATION_FAILED,
    MALFORMED_REQUEST,
    METHOD_NOT_ALLOWED,
    NOT_FOUND,
    RESOURCE_CONFLICT,
    RESOURCE_GONE,
    RATE_LIMITED,


    // Example domain-specific codes — extend as you add features
    USER_NOT_FOUND,
    USER_DUPLICATE_EMAIL,
    APPOINTMENT_SLOT_UNAVAILABLE,
    PAYMENT_REQUIRED,


    // Infrastructure / dependency issues
    DEPENDENCY_FAILURE,


    // Server-side fallback
    INTERNAL_ERROR
}
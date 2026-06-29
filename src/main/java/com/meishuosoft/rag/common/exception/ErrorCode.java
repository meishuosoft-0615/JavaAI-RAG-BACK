package com.meishuosoft.rag.common.exception;

public enum ErrorCode {

    SUCCESS("0", "Success"),
    BAD_REQUEST("400", "Bad request"),
    UNAUTHORIZED("401", "Unauthorized"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Not found"),
    CONFLICT("409", "Conflict"),
    INTERNAL_ERROR("500", "Internal server error"),
    DEPENDENCY_UNAVAILABLE("503", "Dependency unavailable");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}

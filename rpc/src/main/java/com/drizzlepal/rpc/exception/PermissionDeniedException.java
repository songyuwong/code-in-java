package com.drizzlepal.rpc.exception;

import org.springframework.http.HttpStatus;

/**
 * 权限不足异常
 */
public class PermissionDeniedException extends RpcException {

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.FORBIDDEN;
    }

}

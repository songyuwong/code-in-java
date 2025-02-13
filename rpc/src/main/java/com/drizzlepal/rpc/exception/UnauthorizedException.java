package com.drizzlepal.rpc.exception;

import org.springframework.http.HttpStatus;

/**
 * 未授权异常
 */
public class UnauthorizedException extends RpcException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}

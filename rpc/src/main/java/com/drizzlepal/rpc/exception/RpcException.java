package com.drizzlepal.rpc.exception;

import org.springframework.http.HttpStatus;

public abstract class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract HttpStatus httpStatus();

}

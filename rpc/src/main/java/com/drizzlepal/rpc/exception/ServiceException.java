package com.drizzlepal.rpc.exception;

import org.springframework.http.HttpStatus;

/**
 * 服务异常
 */
public class ServiceException extends RpcException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}

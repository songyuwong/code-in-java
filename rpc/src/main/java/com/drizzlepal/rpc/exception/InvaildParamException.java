package com.drizzlepal.rpc.exception;

import org.springframework.http.HttpStatus;

/**
 * 请求参数错误异常
 */
public class InvaildParamException extends RpcException {

    public InvaildParamException(String message) {
        super(message);
    }

    public InvaildParamException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public InvaildParamException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}

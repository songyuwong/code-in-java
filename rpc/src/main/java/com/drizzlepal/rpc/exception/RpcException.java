package com.drizzlepal.rpc.exception;

import org.springframework.http.HttpStatus;

/**
 * RpcException类是专门为RPC（远程过程调用）定义的抽象异常类。
 * 它继承自RuntimeException，用于处理RPC调用过程中的各种异常情况。
 * 该类提供了一种机制，通过覆盖httpStatus方法来指定异常对应的HTTP状态码。
 */
public abstract class RpcException extends RuntimeException {

    /**
     * 构造函数，接收异常消息作为参数。
     * 
     * @param message 异常消息
     */
    public RpcException(String message) {
        super(message);
    }

    /**
     * 构造函数，接收Throwable对象作为参数。
     * 
     * @param cause 引发异常的原因
     */
    public RpcException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /**
     * 构造函数，同时接收异常消息和Throwable对象作为参数。
     * 
     * @param message 异常消息
     * @param cause   引发异常的原因
     */
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 抽象方法，用于获取与异常关联的HTTP状态码。
     * 
     * @return HttpStatus对象，表示HTTP状态码
     */
    public abstract HttpStatus httpStatus();

    /**
     * 静态工厂方法，根据Throwable对象创建一个新的RpcException实例。
     * 默认HTTP状态码为INTERNAL_SERVER_ERROR。
     * 
     * @param cause 引发异常的原因
     * @return 新的RpcException实例
     */
    public static RpcException newRpcException(Throwable cause) {
        return new RpcException(cause) {
            private static final long serialVersionUID = 1L;

            @Override
            public HttpStatus httpStatus() {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        };
    }

    /**
     * 静态工厂方法，根据异常消息创建一个新的RpcException实例。
     * 默认HTTP状态码为INTERNAL_SERVER_ERROR。
     * 
     * @param message 异常消息
     * @return 新的RpcException实例
     */
    public static RpcException newRpcException(String message) {
        return new RpcException(message) {
            private static final long serialVersionUID = 1L;

            @Override
            public HttpStatus httpStatus() {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        };
    }

    /**
     * 静态工厂方法，根据异常消息和Throwable对象创建一个新的RpcException实例。
     * 默认HTTP状态码为INTERNAL_SERVER_ERROR。
     * 
     * @param message 异常消息
     * @param cause   引发异常的原因
     * @return 新的RpcException实例
     */
    public static RpcException newRpcException(String message, Throwable cause) {
        return new RpcException(message, cause) {
            private static final long serialVersionUID = 1L;

            @Override
            public HttpStatus httpStatus() {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        };
    }

    /**
     * 静态工厂方法，根据HTTP状态码和异常消息创建一个新的RpcException实例。
     * 
     * @param httpStatus HTTP状态码
     * @param message    异常消息
     * @return 新的RpcException实例
     */
    public static RpcException newRpcException(HttpStatus httpStatus, String message) {
        return new RpcException(message) {
            private static final long serialVersionUID = 1L;

            @Override
            public HttpStatus httpStatus() {
                return httpStatus;
            }
        };
    }

    /**
     * 静态工厂方法，根据HTTP状态码和Throwable对象创建一个新的RpcException实例。
     * 
     * @param httpStatus HTTP状态码
     * @param cause      引发异常的原因
     * @return 新的RpcException实例
     */
    public static RpcException newRpcException(HttpStatus httpStatus, Throwable cause) {
        return new RpcException(cause) {
            private static final long serialVersionUID = 1L;

            @Override
            public HttpStatus httpStatus() {
                return httpStatus;
            }
        };
    }

    /**
     * 静态工厂方法，根据HTTP状态码、异常消息和Throwable对象创建一个新的RpcException实例。
     * 
     * @param httpStatus HTTP状态码
     * @param message    异常消息
     * @param cause      引发异常的原因
     * @return 新的RpcException实例
     */
    public static RpcException newRpcException(HttpStatus httpStatus, String message, Throwable cause) {
        return new RpcException(message, cause) {
            private static final long serialVersionUID = 1L;

            @Override
            public HttpStatus httpStatus() {
                return httpStatus;
            }
        };
    }

}
package com.drizzlepal.rpc.response;

import com.alibaba.fastjson2.JSON;
import com.drizzlepal.rpc.exception.RpcException;

import lombok.Data;

/**
 * RPC 响应结果
 */
@Data
public abstract class RpcResponse {

    /**
     * 请求响应主体数据
     */
    protected Object data;

    public static RpcFaildResponse failed(Throwable cause) {
        return new RpcFaildResponse(JSON.toJSONString(cause));
    }

    public static RpcFaildResponse failed(RpcException cause) {
        return new RpcFaildResponse(cause.getMessage());
    }

    public static RpcFaildResponse failed(String message) {
        return new RpcFaildResponse(message);
    }

    public static RpcFaildResponse failed(Throwable cause, Object data) {
        return new RpcFaildResponse(JSON.toJSONString(cause), data);
    }

    public static RpcFaildResponse failed(RpcException cause, Object data) {
        return new RpcFaildResponse(cause.getMessage(), data);
    }

    public static RpcFaildResponse failed(String message, Object data) {
        return new RpcFaildResponse(message, data);
    }

    public static RpcSucceedResponse succeed(Object data) {
        return new RpcSucceedResponse(data);
    }

}

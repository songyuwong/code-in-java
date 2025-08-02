package com.drizzlepal.rpc;

import com.drizzlepal.rpc.response.RpcFaildResponse;
import com.drizzlepal.rpc.response.RpcSucceedResponse;

import lombok.Data;

/**
 * RPC 响应结果
 */
@Data
public abstract class RpcResponse {

    /**
     * 响应状态
     */
    protected RpcStatus status;

    /**
     * 请求响应主体数据
     */
    protected Object data;

    public RpcResponse(RpcStatus status, Object data) {
        this.status = status;
        this.data = data;
    }

    public static RpcFaildResponse failed(RpcException cause) {
        return new RpcFaildResponse(cause);
    }

    public static RpcFaildResponse failed(RpcStatus status) {
        return new RpcFaildResponse(status);
    }

    public static RpcFaildResponse failed(RpcStatus status, Object data) {
        return new RpcFaildResponse(status, data);
    }

    public static RpcFaildResponse failed(String message) {
        return new RpcFaildResponse(message);
    }

    public static RpcFaildResponse failed(RpcException rpcException, Object data) {
        return new RpcFaildResponse(rpcException, data);
    }

    public static RpcFaildResponse failed(String message, Object data) {
        return new RpcFaildResponse(message, data);
    }

    public static RpcSucceedResponse succeed(Object data) {
        return new RpcSucceedResponse(data);
    }

}

package com.drizzlepal.rpc.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RPC 失败调用响应
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RpcFaildResponse extends RpcResponse {

    /**
     * 错误信息
     */
    private Object message;

    public RpcFaildResponse(Object message, Object data) {
        this.message = message;
        this.data = data;
    }

}

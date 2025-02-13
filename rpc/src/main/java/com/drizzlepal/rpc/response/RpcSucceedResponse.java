package com.drizzlepal.rpc.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RPC 成功调用响应
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RpcSucceedResponse extends RpcResponse {

    public RpcSucceedResponse(Object data) {
        this.data = data;
    }

}

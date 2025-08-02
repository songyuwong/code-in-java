package com.drizzlepal.rpc.response;

import com.drizzlepal.rpc.RpcResponse;
import com.drizzlepal.rpc.RpcStatusCommon;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RPC 成功调用响应
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RpcSucceedResponse extends RpcResponse {

    public RpcSucceedResponse() {
        super(RpcStatusCommon.SUCCESS, null);
    }

    public RpcSucceedResponse(Object data) {
        super(RpcStatusCommon.SUCCESS, data);
    }

}

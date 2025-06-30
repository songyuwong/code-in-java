package com.drizzlepal.rpc.response;

import com.drizzlepal.rpc.RpcException;
import com.drizzlepal.rpc.RpcResponse;
import com.drizzlepal.rpc.RpcStatus;
import com.drizzlepal.rpc.RpcStatusCommon;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RPC 失败调用响应
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RpcFaildResponse extends RpcResponse {

    private String detail;

    public RpcFaildResponse() {
        super(RpcStatusCommon.UNKNOWN_ERROR, null);
        this.detail = RpcStatusCommon.UNKNOWN_ERROR.getMessage();
    }

    public RpcFaildResponse(RpcStatus status) {
        super(status, null);
        this.detail = status.getMessage();
    }

    public RpcFaildResponse(RpcStatus status, Object data) {
        super(status, data);
        this.detail = status.getMessage();
    }

    public RpcFaildResponse(String detail) {
        super(RpcStatusCommon.UNKNOWN_ERROR, null);
        this.detail = detail;
    }

    public RpcFaildResponse(String detail, Object data) {
        super(RpcStatusCommon.UNKNOWN_ERROR, data);
        this.detail = detail;
    }

    public RpcFaildResponse(RpcException exception) {
        super(exception.getRpcStatus(), exception.getCause());
        this.detail = exception.getMessage();
    }

    public RpcFaildResponse(RpcException exception, Object data) {
        super(exception.getRpcStatus(), data);
        this.detail = exception.getMessage();
    }

    public RpcFaildResponse(RpcStatus status, String detail) {
        super(status, null);
        this.detail = detail;
    }

    public RpcFaildResponse(RpcStatus status, String detail, Object data) {
        super(status, data);
        this.detail = detail;
    }

    public RpcFaildResponse(RpcException exception, String detail) {
        super(exception.getRpcStatus(), exception.getCause());
        this.detail = detail;
    }

    public RpcFaildResponse(RpcException exception, String detail, Object data) {
        super(exception.getRpcStatus(), data);
        this.detail = detail;
    }

    public RpcFaildResponse(RpcStatus status, Throwable cause) {
        super(status, cause);
        this.detail = status.getMessage();
    }

    public RpcFaildResponse(RpcStatus status, String detail, Throwable cause) {
        super(status, cause);
        this.detail = detail;
    }

    public RpcFaildResponse(String detail, Throwable cause) {
        super(RpcStatusCommon.UNKNOWN_ERROR, cause);
        this.detail = detail;
    }

}

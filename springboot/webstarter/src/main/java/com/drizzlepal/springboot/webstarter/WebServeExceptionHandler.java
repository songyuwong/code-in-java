package com.drizzlepal.springboot.webstarter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drizzlepal.rpc.RpcException;
import com.drizzlepal.rpc.RpcResponse;
import com.drizzlepal.rpc.RpcStatusCommon;

import lombok.extern.slf4j.Slf4j;

/**
 * 控制器建议类，处理 REST 控制器中的异常
 * 该类专门处理标注有 @RestController 注解的控制器中的异常
 */
@Slf4j
@ControllerAdvice(annotations = { RestController.class })
public class WebServeExceptionHandler {

    /**
     * 处理自定义的 WebServeException 异常
     * 当捕获到 WebServeException 时，会构建并返回一个包含异常信息的 Response 对象
     *
     * @param ex 抛出的 WebServeException 异常对象
     * @return 包含异常信息的 ResponseEntity 对象
     */
    @ResponseBody
    @ExceptionHandler(RpcException.class)
    public ResponseEntity<RpcResponse> handleWebServeException(RpcException ex) {
        log.error("捕获到异常", ex);
        return ResponseEntity.status(500).body(RpcResponse.failed(ex));
    }

    /**
     * 处理所有未捕获的异常
     * 当捕获到未指定的 Throwable 时，构建并返回一个包含通用错误信息的 Response 对象
     *
     * @param cause 未捕获的 Throwable 对象
     * @return 包含通用错误信息的 ResponseEntity 对象
     */
    @ResponseBody
    @ExceptionHandler({ Throwable.class })
    public ResponseEntity<RpcResponse> handleException(Throwable cause) {
        log.error("未处理的异常", cause);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RpcResponse.failed(RpcStatusCommon.UNKNOWN_ERROR, cause));
    }

}
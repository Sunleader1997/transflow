package org.sunyaxing.transflow.transflowapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sunyaxing.transflow.transflowapp.common.Result;

@Slf4j
@ControllerAdvice
public class GlobeExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Result> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("参数错误：", e);
        return ResponseEntity.ok(Result.fail("参数错误：" + e.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Result> illegalArgumentExceptionHandler(RuntimeException e) {
        return ResponseEntity.ok(Result.fail(e.getMessage()));
    }
}

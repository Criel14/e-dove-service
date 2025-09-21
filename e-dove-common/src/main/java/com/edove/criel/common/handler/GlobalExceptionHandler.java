package com.edove.criel.common.handler;

import com.edove.criel.common.exception.BaseException;
import com.edove.criel.common.exception.ErrorCode;
import com.edove.criel.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 拦截非业务异常：代码报错等
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception e) {
        // TODO Seata: 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
        LOGGER.error("系统异常：{}", e.getMessage());
        return Result.error(ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 拦截业务异常，将异常信息包装后返回
     */
    @ExceptionHandler(BaseException.class)
    public Result<Object> exceptionHandler(BaseException e) {
        LOGGER.error("业务异常：{}", e.getMessage());
        return Result.error(e.getMessage());
    }
}

package com.criel.edove.common.handler;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.result.Result;
import org.apache.seata.core.context.RootContext;
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
        // 检查是否在Seata全局事务中：如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
        if (StrUtil.isNotBlank(RootContext.getXID())) {
            LOGGER.error("Seata全局事务中发生异常：{}", e.getMessage());
            throw new RuntimeException(e); // 重新抛出异常，确保Seata能够捕获并回滚
        }

        LOGGER.error("系统异常：{}", e.getMessage());
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 拦截业务异常，将异常信息包装后返回
     */
    @ExceptionHandler(BizException.class)
    public Result<Object> exceptionHandler(BizException e) {
        LOGGER.error("业务异常：{}-{}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
}

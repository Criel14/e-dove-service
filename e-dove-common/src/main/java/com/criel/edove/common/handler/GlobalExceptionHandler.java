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
        //if (StrUtil.isNotBlank(RootContext.getXID())) {
        if (inSeataGlobalTx()) {
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

    /**
     * 通过反射拿到 RootContext，调用 RootContext.getXID()，如果没引入Seata依赖，不会报错
     */
    private boolean inSeataGlobalTx() {
        try {
            Class<?> clazz = Class.forName("org.apache.seata.core.context.RootContext");
            Object xid = clazz.getMethod("getXID").invoke(null);
            return xid != null && !xid.toString().isBlank();
        } catch (ClassNotFoundException e) {
            return false; // 没有 Seata 依赖，按非分布式事务处理
        } catch (Exception e) {
            return false;
        }
    }
}

package com.criel.edove.common.util;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.result.Result;

import java.util.function.Supplier;

/**
 * 远程调用工具类：执行远程调用，并处理异常
 */
public final class RemoteCallUtil {

    private RemoteCallUtil() {
    }

    /**
     * 执行远程调用，返回result中的data数据 或 抛出异常
     *
     * @param call 远程调用方法，例如 () -> userFeignClient.getUserInfo(userId)
     */
    public static <T> T callAndUnwrap(Supplier<Result<T>> call) {
        Result<T> result = call.get();
        if (result == null) {
            throw new BizException(ErrorCode.SYSTEM_ERROR);
        }
        if (!Boolean.TRUE.equals(result.getStatus())) {
            throw new BizException(result.getCode(), result.getMessage());
        }
        return result.getData();
    }

    /**
     * 执行远程调用，并返回result中的data数据 或 抛出指定异常
     *
     * @param call 远程调用方法，例如 () -> userFeignClient.getUserInfo(userId)
     * @param errorCode 自定义调用错误时的异常信息
     */
    public static <T> T callAndUnwrap(Supplier<Result<T>> call, ErrorCode errorCode) {
        Result<T> result = call.get();
        if (result == null || !Boolean.TRUE.equals(result.getStatus())) {
            throw new BizException(errorCode);
        }
        return result.getData();
    }

}

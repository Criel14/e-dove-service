package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 更新用户信息时：邮箱参数错误异常
 */
public class UpdateInfoEmailParameterException extends BaseException {

    public UpdateInfoEmailParameterException() {
        super(ErrorCode.UPDATE_INFO_EMAIL_PARAMETER_ERROR);
    }

    public UpdateInfoEmailParameterException(String extraMessage) {
        super(ErrorCode.UPDATE_INFO_EMAIL_PARAMETER_ERROR, extraMessage);
    }

}
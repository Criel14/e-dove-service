package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 更新用户信息时：邮箱已存在异常
 */
public class UpdateInfoEmailAlreadyExistsException extends BizException {

    public UpdateInfoEmailAlreadyExistsException() {
        super(ErrorCode.UPDATE_INFO_EMAIL_ALREADY_EXISTS);
    }

    public UpdateInfoEmailAlreadyExistsException(String extraMessage) {
        super(ErrorCode.UPDATE_INFO_EMAIL_ALREADY_EXISTS, extraMessage);
    }

}
package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 更新用户信息时：用户名已存在异常
 */
public class UpdateInfoUsernameAlreadyExistsException extends BaseException {

    public UpdateInfoUsernameAlreadyExistsException() {
        super(ErrorCode.UPDATE_INFO_USERNAME_ALREADY_EXISTS);
    }

    public UpdateInfoUsernameAlreadyExistsException(String extraMessage) {
        super(ErrorCode.UPDATE_INFO_USERNAME_ALREADY_EXISTS, extraMessage);
    }

}
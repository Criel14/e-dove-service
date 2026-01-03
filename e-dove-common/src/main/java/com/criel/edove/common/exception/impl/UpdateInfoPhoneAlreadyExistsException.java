package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 更新用户信息时：手机号已存在异常
 */
public class UpdateInfoPhoneAlreadyExistsException extends BizException {

    public UpdateInfoPhoneAlreadyExistsException() {
        super(ErrorCode.UPDATE_INFO_PHONE_ALREADY_EXISTS);
    }

    public UpdateInfoPhoneAlreadyExistsException(String extraMessage) {
        super(ErrorCode.UPDATE_INFO_PHONE_ALREADY_EXISTS, extraMessage);
    }

}
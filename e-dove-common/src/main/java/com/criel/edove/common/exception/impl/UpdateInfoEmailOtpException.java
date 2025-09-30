package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 更新用户信息时：邮箱验证码错误
 */
public class UpdateInfoEmailOtpException extends BaseException {

    public UpdateInfoEmailOtpException() {
        super(ErrorCode.UPDATE_INFO_EMAIL_OTP_ERROR);
    }

    public UpdateInfoEmailOtpException(String extraMessage) {
        super(ErrorCode.UPDATE_INFO_EMAIL_OTP_ERROR, extraMessage);
    }

}

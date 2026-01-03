package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户注册时：邮箱验证码错误
 */
public class RegisterEmailOtpException extends BizException {

    public RegisterEmailOtpException() {
        super(ErrorCode.REGISTER_EMAIL_OTP_ERROR);
    }

    public RegisterEmailOtpException(String extraMessage) {
        super(ErrorCode.REGISTER_EMAIL_OTP_ERROR, extraMessage);
    }

}

package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户注册时：手机验证码错误
 */
public class RegisterPhoneOtpException extends BizException {

    public RegisterPhoneOtpException() {
        super(ErrorCode.REGISTER_PHONE_OTP_ERROR);
    }

    public RegisterPhoneOtpException(String extraMessage) {
        super(ErrorCode.REGISTER_PHONE_OTP_ERROR, extraMessage);
    }

}

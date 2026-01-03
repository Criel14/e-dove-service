package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户登录时：手机验证码错误
 */
public class UserSignInPhoneOtpException extends BizException {

    public UserSignInPhoneOtpException() {
        super(ErrorCode.SIGN_IN_PHONE_OTP_ERROR);
    }

    public UserSignInPhoneOtpException(String extraMessage) {
        super(ErrorCode.SIGN_IN_PHONE_OTP_ERROR, extraMessage);
    }

}

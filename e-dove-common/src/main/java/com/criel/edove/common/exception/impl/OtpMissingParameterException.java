package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 验证码请求参数缺失
 */
public class OtpMissingParameterException extends BizException {

    public OtpMissingParameterException() {
        super(ErrorCode.OTP_MISSING_PARAMETER);
    }

    public OtpMissingParameterException(String extraMessage) {
        super(ErrorCode.OTP_MISSING_PARAMETER, extraMessage);
    }

}

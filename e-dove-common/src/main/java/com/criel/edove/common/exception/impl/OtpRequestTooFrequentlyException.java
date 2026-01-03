package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 验证码请求频率过快
 */
public class OtpRequestTooFrequentlyException extends BizException {

    public OtpRequestTooFrequentlyException() {
        super(ErrorCode.OTP_REQUEST_TOO_FREQUENTLY);
    }

    public OtpRequestTooFrequentlyException(String extraMessage) {
        super(ErrorCode.OTP_REQUEST_TOO_FREQUENTLY, extraMessage);
    }

}

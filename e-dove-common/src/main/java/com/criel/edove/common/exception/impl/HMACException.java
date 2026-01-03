package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * HMAC-SHA256计算异常
 */
public class HMACException extends BizException {

    public HMACException() {
        super(ErrorCode.HMAC_ERROR);
    }

    public HMACException(String extraMessage) {
        super(ErrorCode.HMAC_ERROR, extraMessage);
    }

}

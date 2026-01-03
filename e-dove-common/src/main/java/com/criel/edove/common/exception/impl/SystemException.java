package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 系统异常
 */
public class SystemException extends BizException {

    public SystemException() {
        super(ErrorCode.SYSTEM_ERROR);
    }

    public SystemException(String extraMessage) {
        super(ErrorCode.SYSTEM_ERROR, extraMessage);
    }

}

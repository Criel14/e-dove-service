package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 系统异常
 */
public class SystemException extends BaseException {

    public SystemException() {
        super(ErrorCode.SYSTEM_ERROR);
    }

    public SystemException(String extraMessage) {
        super(ErrorCode.SYSTEM_ERROR, extraMessage);
    }

}

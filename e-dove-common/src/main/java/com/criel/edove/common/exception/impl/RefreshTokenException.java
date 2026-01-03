package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * token 刷新异常
 */
public class RefreshTokenException extends BizException {

    public RefreshTokenException() {
        super(ErrorCode.REFRESH_TOKEN_ERROR);
    }

    public RefreshTokenException(String extraMessage) {

        super(ErrorCode.REFRESH_TOKEN_ERROR, extraMessage);
    }
}

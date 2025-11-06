package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 解绑门店时，用户未绑定该门店
 */
public class UserStoreBoundNotMatchedException extends BaseException {

    public UserStoreBoundNotMatchedException() {
        super(ErrorCode.USER_STORE_BOUND_NOT_MATCHED);
    }

    public UserStoreBoundNotMatchedException(String extraMessage) {
        super(ErrorCode.USER_STORE_BOUND_NOT_MATCHED, extraMessage);
    }

}

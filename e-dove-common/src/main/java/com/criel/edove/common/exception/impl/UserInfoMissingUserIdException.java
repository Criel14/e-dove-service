package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 创建用户信息时缺少用户ID
 */
public class UserInfoMissingUserIdException extends BaseException {

    public UserInfoMissingUserIdException() {
        super(ErrorCode.USERINFO_MISSING_USER_ID);
    }

    public UserInfoMissingUserIdException(String extraMessage) {
        super(ErrorCode.USERINFO_MISSING_USER_ID, extraMessage);
    }

}

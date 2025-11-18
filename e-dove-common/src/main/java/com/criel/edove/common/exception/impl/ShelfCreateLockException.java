package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 新建货架时获取分布式锁失败
 */
public class ShelfCreateLockException extends BaseException {

    public ShelfCreateLockException() {
        super(ErrorCode.SHELF_CREATE_LOCK_ERROR);
    }

    public ShelfCreateLockException(String extraMessage) {
        super(ErrorCode.SHELF_CREATE_LOCK_ERROR, extraMessage);
    }

}

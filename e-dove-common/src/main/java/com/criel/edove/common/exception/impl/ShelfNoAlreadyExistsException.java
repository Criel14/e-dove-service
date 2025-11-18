package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 货架编号重复异常
 */
public class ShelfNoAlreadyExistsException extends BaseException {

    public ShelfNoAlreadyExistsException() {
        super(ErrorCode.SHELF_NO_ALREADY_EXISTS);
    }

    public ShelfNoAlreadyExistsException(String extraMessage) {
        super(ErrorCode.SHELF_NO_ALREADY_EXISTS, extraMessage);
    }

}

package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 删除货架层时，货架层上仍有包裹
 */
public class ShelfLayerHasParcelsException extends BaseException {

    public ShelfLayerHasParcelsException() {
        super(ErrorCode.SHELF_LAYER_HAS_PARCELS);
    }

    public ShelfLayerHasParcelsException(String extraMessage) {
        super(ErrorCode.SHELF_LAYER_HAS_PARCELS, extraMessage);
    }

}

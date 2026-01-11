package com.criel.edove.store.service;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.store.dto.LayerReduceCountDTO;
import com.criel.edove.store.dto.ParcelCheckInDTO;
import com.criel.edove.store.dto.ShelfDTO;
import com.criel.edove.store.dto.ShelfQueryDTO;
import com.criel.edove.store.entity.Shelf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.criel.edove.store.vo.ParcelCheckInVO;
import com.criel.edove.store.vo.ShelfAndLayerVO;

/**
 * 货架服务
 *
 * @author Criel
 * @since 2025-10-02
 */
public interface ShelfService extends IService<Shelf> {

    void createShelf(ShelfDTO shelfDTO);

    PageResult<ShelfAndLayerVO> queryShelfAndLayer(ShelfQueryDTO shelfQueryDTO);

    void updateShelf(ShelfDTO shelfDTO);

    void layerReduceCount(LayerReduceCountDTO layerReduceCountDTO);

    ParcelCheckInVO parcelCheckIn(ParcelCheckInDTO parcelCheckInDTO);
}

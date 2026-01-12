package com.criel.edove.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.criel.edove.store.entity.Shelf;
import com.criel.edove.store.entity.ShelfLayer;
import com.criel.edove.store.vo.ShelfAndLayerVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
 * <p>
 * 货架表 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Mapper
public interface ShelfMapper extends BaseMapper<Shelf> {

    Integer selectMaxShelfNo(Long storeId);

    /**
     * 一次性查出【货架+货架层】（由于在分页查询时存在一些问题，所以弃用）
     */
    IPage<ShelfAndLayerVO> selectShelfAndLayerByStoreId(IPage<?> page, Long storeId);

    int reduceCurrentCount(Long storeId, Integer shelfNo, Integer layerNo);

    ShelfLayer selectOneBestFit(Long storeId, BigDecimal weight, BigDecimal height, BigDecimal width, BigDecimal length);

}

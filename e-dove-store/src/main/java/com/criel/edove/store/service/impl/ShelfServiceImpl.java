package com.criel.edove.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.enumeration.ShelfStatusEnum;
import com.criel.edove.common.exception.impl.ShelfCreateLockException;
import com.criel.edove.common.exception.impl.ShelfNoAlreadyExistsException;
import com.criel.edove.common.exception.impl.UserStoreNotBoundException;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.store.dto.ShelfDTO;
import com.criel.edove.store.entity.Shelf;
import com.criel.edove.store.entity.ShelfLayer;
import com.criel.edove.store.mapper.ShelfLayerMapper;
import com.criel.edove.store.mapper.ShelfMapper;
import com.criel.edove.store.service.ShelfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 货架服务
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class ShelfServiceImpl extends ServiceImpl<ShelfMapper, Shelf> implements ShelfService {

    private final ShelfMapper shelfMapper;
    private final ShelfLayerMapper shelfLayerMapper;
    private final SnowflakeService snowflakeService;
    private final UserFeignClient userFeignClient;
    private final RedissonClient redissonClient;

    /**
     * 创建货架，并根据层数自动创建【货架层】，最大编号上限默认为999
     */
    @Override
    @Transactional
    public void createShelf(ShelfDTO shelfDTO) {
        // 获取用户所属门店
        Result<Long> result = userFeignClient.getUserStoreId();
        if (result.getData() == null) {
            throw new UserStoreNotBoundException();
        }
        Long storeId = result.getData();

        // 生成新的货架id
        long shelfId = snowflakeService.nextId();

        // 分布式锁防止并发生成相同的【货架编号】
        String lockKey = RedisKeyConstant.SHELF_CREATE_LOCK + storeId;
        RLock rLock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = rLock.tryLock(10, TimeUnit.SECONDS);
            if (!locked) {
                throw new ShelfCreateLockException();
            }
            // 获取货架编号
            int shelfNo = getShelfNo(shelfDTO.getShelfNo(), storeId);
            // 创建货架Shelf
            insertShelf(shelfDTO, shelfId, storeId, shelfNo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 创建货架层ShelfLayer
        insertShelfLayer(shelfDTO.getLayerCount(), shelfId);
    }

    /**
     * 新建货架层，批量插入数据库
     */
    private void insertShelfLayer(int layerCount, long shelfId) {
        final int todayMaxSeq = 0;
        final int maxCapacity = 999;
        // 批量插入
        List<ShelfLayer> shelfLayers = new ArrayList<>();
        for (int layerNo = 1; layerNo <= layerCount; layerNo++) {
            ShelfLayer shelfLayer = new ShelfLayer();
            shelfLayer.setShelfId(shelfId); // 所属货架ID
            shelfLayer.setTodayMaxSeq(todayMaxSeq); // 当前最大序号
            shelfLayer.setMaxCapacity(maxCapacity); // 最大编号上限
            shelfLayer.setId(snowflakeService.nextId()); // 货架层ID
            shelfLayer.setLayerNo(layerNo); // 货架层编号
            shelfLayers.add(shelfLayer);
        }
        shelfLayerMapper.insert(shelfLayers);
    }

    /**
     * 新建货架，插入数据库
     */
    private void insertShelf(ShelfDTO shelfDTO, long shelfId, Long storeId, int shelfNo) {
        Shelf shelf = new Shelf();
        BeanUtils.copyProperties(shelfDTO, shelf);
        shelf.setId(shelfId); // 货架ID
        shelf.setStoreId(storeId); // 所属门店ID
        shelf.setShelfNo(shelfNo); // 门店内部货架编号
        shelf.setStatus(ShelfStatusEnum.ENABLE.getCode()); // 默认状态
        shelfMapper.insert(shelf);
    }

    /**
     * 获取货架编号
     *
     * @param shelfNo 前端传来的货架编号，可能为null
     * @return 返回新创建的货架编号
     */
    private int getShelfNo(Integer shelfNo, Long storeId) {
        if (shelfNo != null) {
            // 检查传入的货架编号是否存在
            LambdaQueryWrapper<Shelf> shelfWrapper = new LambdaQueryWrapper<>();
            shelfWrapper.eq(Shelf::getShelfNo, shelfNo);
            shelfWrapper.eq(Shelf::getStoreId, storeId);
            boolean exists = shelfMapper.exists(shelfWrapper);
            if (exists) {
                throw new ShelfNoAlreadyExistsException();
            }
            // 不存在则指定为传入的编号
            return shelfNo;
        } else {
            // 获取当前门店最大编号
            Integer maxShelfNo = shelfMapper.selectMaxShelfNo(storeId);
            // 自动生成货架编号：最大货架编号 + 1
            return maxShelfNo == null ? 1 : maxShelfNo + 1;
        }
    }

}

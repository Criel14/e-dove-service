package com.criel.edove.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.enumeration.ShelfStatusEnum;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.exception.impl.*;
import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.store.dto.LayerReduceCountDTO;
import com.criel.edove.store.dto.ParcelCheckInDTO;
import com.criel.edove.store.dto.ShelfDTO;
import com.criel.edove.store.dto.ShelfQueryDTO;
import com.criel.edove.store.entity.Shelf;
import com.criel.edove.store.entity.ShelfLayer;
import com.criel.edove.store.mapper.ShelfLayerMapper;
import com.criel.edove.store.mapper.ShelfMapper;
import com.criel.edove.store.service.ShelfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.store.vo.ParcelCheckInVO;
import com.criel.edove.store.vo.ShelfAndLayerVO;
import com.criel.edove.store.vo.ShelfLayerVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        Long storeId = getUserStoreId();

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
        } finally {
            // 释放锁
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

        // 创建货架层ShelfLayer
        insertShelfLayer(shelfDTO.getLayerCount(), shelfId, 0);
    }

    /**
     * 分页查询货架 + 货架层
     */
    @Override
    public PageResult<ShelfAndLayerVO> queryShelfAndLayer(ShelfQueryDTO shelfQueryDTO) {
        // 这2个参数指的是【货架】，而不是【货架层】
        int pageNum = shelfQueryDTO.getPageNum();
        int pageSize = shelfQueryDTO.getPageSize();

        // 获取用户所属门店
        Long storeId = getUserStoreId();

        // 分页查询用户所属门店的【货架】
        IPage<Shelf> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Shelf> shelfWrapper = new LambdaQueryWrapper<>();
        shelfWrapper.eq(Shelf::getStoreId, storeId)
                .orderByAsc(Shelf::getShelfNo);
        IPage<Shelf> shelfIPage = shelfMapper.selectPage(page, shelfWrapper);
        List<Shelf> shelves = shelfIPage.getRecords();
        List<Long> shelfIds = shelves.stream()
                .map(Shelf::getId)
                .toList();

        // 空则提前返回，否则mybatis-plus会生成"IN ()"这样的内容
        if (shelves.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), shelfIPage.getTotal());
        }

        // 查询这些货架所对应的【货架层】
        LambdaQueryWrapper<ShelfLayer> shelfLayerWrapper = new LambdaQueryWrapper<>();
        shelfLayerWrapper.in(ShelfLayer::getShelfId, shelfIds)
                .orderByAsc(ShelfLayer::getLayerNo);
        List<ShelfLayer> shelfLayers = shelfLayerMapper.selectList(shelfLayerWrapper);

        // 先把【货架层】映射成Map
        Map<Long, List<ShelfLayerVO>> shelfLayerVOMap = shelfLayers.stream()
                .map(shelfLayer -> {
                    ShelfLayerVO shelfLayerVO = new ShelfLayerVO();
                    BeanUtils.copyProperties(shelfLayer, shelfLayerVO);
                    return shelfLayerVO;
                })
                .collect(Collectors.groupingBy(ShelfLayerVO::getShelfId));

        // 遍历【货架】然后封装结果
        List<ShelfAndLayerVO> shelfAndLayerVOs = new ArrayList<>(shelves.size());
        shelves.forEach(shelf -> {
            // 拿到对应的【货架层】
            List<ShelfLayerVO> shelfLayerVOs = shelfLayerVOMap.getOrDefault(shelf.getId(), Collections.emptyList());
            // 封装数据
            ShelfAndLayerVO shelfAndLayerVO = new ShelfAndLayerVO();
            BeanUtils.copyProperties(shelf, shelfAndLayerVO);
            shelfAndLayerVO.setShelfLayers(shelfLayerVOs);
            // 插入数据
            shelfAndLayerVOs.add(shelfAndLayerVO);
        });

        return new PageResult<>(shelfAndLayerVOs, shelfIPage.getTotal());
    }

    /**
     * 更新货架，包括停用启用、层数变动
     */
    @Override
    @Transactional
    public void updateShelf(ShelfDTO shelfDTO) {
        Long shelfId = shelfDTO.getId();
        // 获取用户所属门店
        Long storeId = getUserStoreId();

        // 分布式锁（粒度是货架）
        String lockKey = RedisKeyConstant.SHELF_UPDATE_LOCK + shelfId;
        RLock rLock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = rLock.tryLock(5, TimeUnit.SECONDS);
            if (!locked) {
                throw new ShelfLayerCountLockException();
            }

            // 查询原始货架数据
            Shelf shelf = shelfMapper.selectById(shelfId);

            // 验证货架所属门店
            if (!Objects.equals(shelf.getStoreId(), storeId)) {
                throw new ShelfNotBelongToStoreException();
            }

            // 验证【货架编号】是否符合条件
            if (!Objects.equals(shelf.getShelfNo(), shelfDTO.getShelfNo())) {
                // 检查传入的货架编号是否存在
                boolean exists = existsShelfNo(shelfDTO.getShelfNo(), storeId);
                if (exists) {
                    throw new ShelfNoAlreadyExistsException();
                }
            }

            // 更新【货架层数】
            int currentLayerCount = shelf.getLayerCount();
            int newLayerCount = shelfDTO.getLayerCount();
            if (newLayerCount > currentLayerCount) { // 若增加货架层，则直接插入新的层
                insertShelfLayer(newLayerCount - currentLayerCount, shelfId, currentLayerCount);

            } else if (newLayerCount < currentLayerCount) { // 若减少货架层，需要判断多余的货架层上是否有包裹
                // 查询原始货架层数据
                LambdaQueryWrapper<ShelfLayer> shelfLayerWrapper = new LambdaQueryWrapper<>();
                shelfLayerWrapper.eq(ShelfLayer::getShelfId, shelfId);
                List<ShelfLayer> shelfLayers = shelfLayerMapper.selectList(shelfLayerWrapper);
                // 筛选出多余的货架层
                List<ShelfLayer> redundantLayers = shelfLayers.stream()
                        .filter(shelfLayer -> shelfLayer.getLayerNo() > newLayerCount)
                        .toList();

                // 遍历检查多余的货架层上是否有包裹
                redundantLayers.forEach(shelfLayer -> {
                    if (shelfLayer.getCurrentCount() > 0) {
                        throw new ShelfLayerHasParcelsException();
                    }
                });

                // 删除多余的货架层
                List<Long> ids = redundantLayers.stream()
                        .map(ShelfLayer::getId)
                        .toList();
                shelfLayerMapper.deleteByIds(ids);
            }

            // 更新货架Shelf数据
            BeanUtils.copyProperties(shelfDTO, shelf);
            shelfMapper.updateById(shelf);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    /**
     * 扣减包裹所在货架层的当前包裹数
     */
    @Override
    public void layerReduceCount(LayerReduceCountDTO layerReduceCountDTO) {
        Long storeId = layerReduceCountDTO.getStoreId();
        Integer shelfNo = layerReduceCountDTO.getShelfNo();
        Integer layerNo = layerReduceCountDTO.getLayerNo();

        // 查到货架ID来使用分布式锁
        LambdaQueryWrapper<Shelf> shelfWrapper = new LambdaQueryWrapper<>();
        shelfWrapper.eq(Shelf::getStoreId, storeId);
        shelfWrapper.eq(Shelf::getShelfNo, shelfNo);
        Shelf shelf = shelfMapper.selectOne(shelfWrapper);
        if (shelf == null) {
            throw new BizException(ErrorCode.SHELF_NOT_FOUND);
        }
        Long shelfId = shelf.getId();

        // 分布式锁（粒度是货架）
        String lockKey = RedisKeyConstant.SHELF_UPDATE_LOCK + shelfId;
        RLock rLock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = rLock.tryLock(5, TimeUnit.SECONDS);
            if (!locked) {
                throw new BizException(ErrorCode.SHELF_LAYER_COUNT_LOCK_ERROR);
            }

            int affected = shelfMapper.reduceCurrentCount(storeId, shelfNo, layerNo);
            if (affected == 0) {
                // 更新失败（可能没找到 或 current_count 已经是 0）
                throw new BizException(ErrorCode.SHELF_LAYER_COUNT_REDUCE_ERROR);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

    }

    /**
     * 为包裹选择合适的货架层，并生成取件码
     *
     * @param parcelCheckInDTO 包裹信息
     * @return 取件码
     */
    @Override
    public ParcelCheckInVO parcelCheckIn(ParcelCheckInDTO parcelCheckInDTO) {
        // 取件码
        StringBuilder pickCodeBuilder = new StringBuilder();

        // 查找到门店所有符合大小/重量条件的货架，找到有空位的第一个的货架层
        ShelfLayer shelfLayer = shelfMapper.selectOneBestFit(
                parcelCheckInDTO.getStoreId(),
                parcelCheckInDTO.getWeight(),
                parcelCheckInDTO.getHeight(),
                parcelCheckInDTO.getWidth(),
                parcelCheckInDTO.getLength()
        );

        // 找不到合适的就抛异常
        // TODO 思考下后面业务上怎么处理
        if (shelfLayer == null) {
            throw new BizException(ErrorCode.NO_AVAILABLE_SHELF_LAYER);
        }

        // 需要重新查找到货架的编号
        Shelf shelf = shelfMapper.selectById(shelfLayer.getShelfId());

        // 分布式锁（粒度是货架）
        String lockKey = RedisKeyConstant.SHELF_UPDATE_LOCK + shelf.getId();
        RLock rLock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = rLock.tryLock(5, TimeUnit.SECONDS);
            if (!locked) {
                throw new BizException(ErrorCode.SHELF_LAYER_COUNT_LOCK_ERROR);
            }

            // 更新货架层的【当前包裹数】和【当天最大取件码序号】
            shelfLayer.setCurrentCount(shelfLayer.getCurrentCount() + 1);
            shelfLayer.setTodayMaxSeq(shelfLayer.getTodayMaxSeq() + 1);
            shelfLayerMapper.updateById(shelfLayer);

            // 生成取件码
            int week = LocalDate.now().getDayOfWeek().getValue();
            pickCodeBuilder.append(shelf.getShelfNo())
                    .append("-")
                    .append(shelfLayer.getLayerNo())
                    .append("-")
                    .append(week)
                    .append(String.format("%03d", shelfLayer.getTodayMaxSeq()));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

        return new ParcelCheckInVO(pickCodeBuilder.toString());
    }

    /**
     * 获取用户所属门店：远程调用user服务
     */
    private Long getUserStoreId() {
        Result<Long> result = userFeignClient.getUserStoreId();
        if (result.getData() == null) {
            throw new UserStoreNotBoundException();
        }
        return result.getData();
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
            boolean exists = existsShelfNo(shelfNo, storeId);
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

    /**
     * 查询门店storeId里，是否存在传入的货架编号shelfNo
     */
    private boolean existsShelfNo(Integer shelfNo, Long storeId) {
        LambdaQueryWrapper<Shelf> shelfWrapper = new LambdaQueryWrapper<>();
        shelfWrapper.eq(Shelf::getShelfNo, shelfNo);
        shelfWrapper.eq(Shelf::getStoreId, storeId);
        return shelfMapper.exists(shelfWrapper);
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
     * 新建货架层，批量插入数据库
     *
     * @param layerCount    需要插入的货架层数
     * @param curMaxLayerNo 当前最大货架层编号，从 curMaxLayerNo + 1 开始插入数据
     */
    private void insertShelfLayer(int layerCount, long shelfId, int curMaxLayerNo) {
        final int todayMaxSeq = 0;
        final int maxCapacity = 999;
        // 批量插入
        List<ShelfLayer> shelfLayers = new ArrayList<>();
        for (int layerNo = curMaxLayerNo + 1; layerNo <= layerCount; layerNo++) {
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

}

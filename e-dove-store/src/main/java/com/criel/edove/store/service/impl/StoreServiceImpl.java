package com.criel.edove.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.criel.edove.common.exception.impl.UserStoreNotBoundException;
import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.vo.UserInfoVO;
import com.criel.edove.store.entity.Store;
import com.criel.edove.store.mapper.StoreMapper;
import com.criel.edove.store.service.StoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.store.vo.StoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门店信息服务
 *
 * @author Criel
 * @since 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    private final UserFeignClient userFeignClient;
    private final StoreMapper storeMapper;

    /**
     * 分页查询所有门店信息
     */
    @Override
    public PageResult<StoreVO> page(int pageNum, int pageSize, String storeName) {
        Page<Store> page = new Page<>(pageNum, pageSize);
        IPage<StoreVO> storePage = storeMapper.selectPageByStoreName(page, storeName);
        return new PageResult<>(storePage.getRecords(), storePage.getTotal());
    }

    /**
     * 查询用户所属门店信息，若未绑定门店，则抛出“未绑定门店异常”
     */
    @Override
    public StoreVO getStoreInfoByUser() {
        // （远程调用）获取用户信息
        Result<UserInfoVO> userInfoResult = userFeignClient.getUserInfo();
        UserInfoVO userInfoVO = userInfoResult.getData();

        // 检查用户是否绑定门店
        if (userInfoVO.getStoreId() == null) {
            throw new UserStoreNotBoundException();
        }

        // 查询门店信息
        Store store = storeMapper.selectById(userInfoVO.getStoreId());
        StoreVO storeVO = new StoreVO();
        BeanUtils.copyProperties(store, storeVO);

        return storeVO;
    }

}

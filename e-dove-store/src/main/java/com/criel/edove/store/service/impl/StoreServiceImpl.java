package com.criel.edove.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.enumeration.StoreStatusEnum;
import com.criel.edove.common.exception.impl.StoreNotFoundException;
import com.criel.edove.common.exception.impl.UserStoreBoundException;
import com.criel.edove.common.exception.impl.UserStoreBoundNotMatchedException;
import com.criel.edove.common.exception.impl.UserStoreNotBoundException;
import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.dto.UpdateUserInfoDTO;
import com.criel.edove.feign.user.vo.UserInfoVO;
import com.criel.edove.store.dto.StoreBindDTO;
import com.criel.edove.store.dto.StoreDTO;
import com.criel.edove.store.entity.Store;
import com.criel.edove.store.mapper.StoreMapper;
import com.criel.edove.store.service.StoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.store.vo.StoreVO;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    private final SnowflakeService snowflakeService;

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
        // 远程调用获取用户所属门店ID
        Result<Long> result = userFeignClient.getUserStoreId();
        if (result.getData() == null) {
            throw new UserStoreNotBoundException();
        }
        Long storeId = result.getData();

        // 查询门店信息
        Store store = storeMapper.selectById(storeId);
        StoreVO storeVO = new StoreVO();
        BeanUtils.copyProperties(store, storeVO);

        return storeVO;
    }

    /**
     * （店长）创建门店
     * 如果店长信息为空，则设置店长信息为当前用户；
     * 同时需要绑定用户与门店（远程调用）
     */
    @Override
    @GlobalTransactional
    public StoreVO createStore(StoreDTO storeDTO) {
        Store store = new Store();
        // 门店ID
        long storeId = snowflakeService.nextId();
        store.setId(storeId);
        // 拷贝其他字段
        BeanUtils.copyProperties(storeDTO, store);
        // 设置店长信息
        if (storeDTO.getManagerUserId() == null && StrUtil.isEmpty(storeDTO.getManagerPhone())) {
            UserInfoContext userInfoContext = UserInfoContextHolder.getUserInfoContext();
            store.setManagerUserId(userInfoContext.getUserId());
            store.setManagerPhone(userInfoContext.getPhone());
        }
        // 初始化门店状态：营业
        store.setStatus(StoreStatusEnum.OPEN.getCode());
        // 插入门店信息
        storeMapper.insert(store);

        // 绑定用户与门店
        try {
            userFeignClient.updateStoreBind(storeId);
        } catch (Exception e) {
            throw new UserStoreBoundException();
        }

        // 返回门店信息
        StoreVO storeVO = new StoreVO();
        BeanUtils.copyProperties(store, storeVO);
        return storeVO;
    }

    /**
     * （店长）修改门店信息 / 修改门店营业状态
     * tip：2个接口用同一个方法
     */
    @Override
    public StoreVO updateStore(StoreDTO storeDTO) {
        // 检查门店ID
        Long storeId = storeDTO.getId();
        if (storeId == null) {
            throw new StoreNotFoundException();
        }

        // 更新数据
        Store updateStore = new Store();
        BeanUtils.copyProperties(storeDTO, updateStore);
        storeMapper.updateById(updateStore);

        // 返回门店信息
        Store store = storeMapper.selectById(storeDTO.getId());
        StoreVO storeVO = new StoreVO();
        BeanUtils.copyProperties(store, storeVO);
        return storeVO;
    }

    /**
     * 删除门店
     */
    @Override
    public void deleteStore(Long storeId) {
        if (storeId == null) {
            throw new StoreNotFoundException();
        }
        storeMapper.deleteById(storeId);
    }

    /**
     * （店长/店员）绑定当前用户与门店
     */
    @Override
    @GlobalTransactional
    public void bindStore(Long storeId) {
        // 判断门店是否存在
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new StoreNotFoundException();
        }

        // 绑定用户与门店
        try {
            userFeignClient.updateStoreBind(storeId);
        } catch (Exception e) {
            throw new UserStoreBoundException();
        }
    }

    /**
     * （店长/店员）解绑当前用户与门店
     */
    @Override
    @GlobalTransactional
    public void unbindStore(Long storeId) {
        // 判断门店是否存在
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new StoreNotFoundException();
        }

        // （远程调用）检查用户是否绑定该门店
        Result<UserInfoVO> userInfoResult = userFeignClient.getUserInfo();
        UserInfoVO userInfoVO = userInfoResult.getData();
        if (!Objects.equals(storeId, userInfoVO.getStoreId())) {
            throw new UserStoreBoundNotMatchedException();
        }

        // 解绑用户与门店
        try {
            userFeignClient.updateStoreBind(null);
        } catch (Exception e) {
            throw new UserStoreBoundException();
        }

    }

}

package com.criel.edove.user.service.impl;

import com.criel.edove.user.entity.UserAddress;
import com.criel.edove.user.mapper.UserAddressMapper;
import com.criel.edove.user.service.UserAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储用户的收货地址信息，支持设置默认地址 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

}

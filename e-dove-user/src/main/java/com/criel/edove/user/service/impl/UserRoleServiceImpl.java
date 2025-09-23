package com.criel.edove.user.service.impl;

import com.criel.edove.user.entity.UserRole;
import com.criel.edove.user.mapper.UserRoleMapper;
import com.criel.edove.user.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色的关联关系表，支持一个用户拥有多个角色 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}

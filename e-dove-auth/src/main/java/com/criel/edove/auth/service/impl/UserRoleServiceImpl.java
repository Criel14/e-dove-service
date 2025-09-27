package com.criel.edove.auth.service.impl;

import com.criel.edove.auth.entity.UserRole;
import com.criel.edove.auth.mapper.UserRoleMapper;
import com.criel.edove.auth.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色的关联关系表，支持一个用户拥有多个角色 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}

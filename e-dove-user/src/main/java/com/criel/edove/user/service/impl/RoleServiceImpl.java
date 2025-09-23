package com.criel.edove.user.service.impl;

import com.criel.edove.user.entity.Role;
import com.criel.edove.user.mapper.RoleMapper;
import com.criel.edove.user.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定义系统中的角色类型 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}

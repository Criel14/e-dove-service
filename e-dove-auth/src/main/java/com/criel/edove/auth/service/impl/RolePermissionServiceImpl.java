package com.criel.edove.auth.service.impl;

import com.criel.edove.auth.entity.RolePermission;
import com.criel.edove.auth.mapper.RolePermissionMapper;
import com.criel.edove.auth.service.RolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色与权限的关联关系表，实现灵活的权限控制 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}

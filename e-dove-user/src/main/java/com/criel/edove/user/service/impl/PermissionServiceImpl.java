package com.criel.edove.user.service.impl;

import com.criel.edove.user.entity.Permission;
import com.criel.edove.user.mapper.PermissionMapper;
import com.criel.edove.user.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定义系统中具体的操作权限点 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}

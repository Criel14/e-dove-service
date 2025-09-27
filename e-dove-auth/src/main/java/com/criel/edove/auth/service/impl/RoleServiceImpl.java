package com.criel.edove.auth.service.impl;

import com.criel.edove.auth.entity.Role;
import com.criel.edove.auth.mapper.RoleMapper;
import com.criel.edove.auth.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定义系统中的角色类型 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}

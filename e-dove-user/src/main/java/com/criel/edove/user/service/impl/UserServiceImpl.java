package com.criel.edove.user.service.impl;

import com.criel.edove.user.entity.User;
import com.criel.edove.user.mapper.UserMapper;
import com.criel.edove.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}

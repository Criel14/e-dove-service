package com.criel.edove.auth.service.impl;

import com.criel.edove.auth.entity.UserAuth;
import com.criel.edove.auth.mapper.UserAuthMapper;
import com.criel.edove.auth.service.UserAuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户认证表，存储登录凭证和状态 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {

}

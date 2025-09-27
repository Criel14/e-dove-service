package com.criel.edove.auth.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.entity.UserAuth;
import com.criel.edove.auth.mapper.UserAuthMapper;
import com.criel.edove.common.exception.impl.UserSignInPasswordException;
import com.criel.edove.common.exception.impl.UserSignInPasswordNotFoundException;
import com.criel.edove.common.exception.impl.UserNotFoundException;
import com.criel.edove.auth.strategy.PreSignInStrategy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 用户登录策略：手机号 + 密码
 */
@Component
@RequiredArgsConstructor
public class PhonePasswordPreSignInStrategy implements PreSignInStrategy {

    private final Logger LOGGER = LoggerFactory.getLogger(PhonePasswordPreSignInStrategy.class);

    private final UserAuthMapper userAuthMapper;

    private final PasswordEncoder passwordEncoder;

    /**
     * @param signInDTO 调用时保证【手机号】和【密码】不为空
     * @return 登录的用户认证信息
     */
    @Override
    public UserAuth preSignIn(SignInDTO signInDTO) {
        LOGGER.info("用户登录策略：手机号（{}） + 密码", signInDTO.getPhone());

        // 查找用户信息 并检查用户是否存在
        String phone = signInDTO.getPhone();
        LambdaQueryWrapper<UserAuth> userAuthWrapper = new LambdaQueryWrapper<>();
        userAuthWrapper.eq(UserAuth::getPhone, phone);
        UserAuth userAuth = userAuthMapper.selectOne(userAuthWrapper);
        if (userAuth == null) {
            throw new UserNotFoundException();
        }

        // 检查用户是否设置了密码
        if (StrUtil.isEmpty(userAuth.getPassword())) {
            throw new UserSignInPasswordNotFoundException();
        }

        // 校验密码（比对【密码原文】和【存储的哈希值】）
        boolean isMatched = passwordEncoder.matches(signInDTO.getPassword(), userAuth.getPassword());
        if (!isMatched) {
            throw new UserSignInPasswordException();
        }

        return userAuth;
    }

}

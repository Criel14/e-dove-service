package com.criel.edove.auth.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.entity.UserAuth;
import com.criel.edove.auth.mapper.UserAuthMapper;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.auth.strategy.PreSignInStrategy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 用户登录策略：邮箱 + 密码
 */
@Component
@RequiredArgsConstructor
public class EmailPasswordPreSignInStrategy implements PreSignInStrategy {

    private final Logger LOGGER = LoggerFactory.getLogger(EmailPasswordPreSignInStrategy.class);

    private final UserAuthMapper userAuthMapper;

    private final PasswordEncoder passwordEncoder;

    /**
     * @param signInDTO 调用时保证【邮箱】和【密码】不为空
     * @return 登录的用户认证信息
     */
    @Override
    public UserAuth preSignIn(SignInDTO signInDTO) {
        LOGGER.info("用户登录策略：邮箱（{}） + 密码", signInDTO.getEmail());

        // 查找用户信息 并检查用户是否存在
        String email = signInDTO.getEmail();
        LambdaQueryWrapper<UserAuth> userAuthWrapper = new LambdaQueryWrapper<>();
        userAuthWrapper.eq(UserAuth::getEmail, email);
        UserAuth userAuth = userAuthMapper.selectOne(userAuthWrapper);
        if (userAuth == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        // 检查用户是否设置了密码
        if (StrUtil.isEmpty(userAuth.getPassword())) {
            throw new BizException(ErrorCode.PASSWORD_NOT_FOUND);
        }

        // 校验密码（比对【密码原文】和【存储的哈希值】）
        boolean isMatched = passwordEncoder.matches(signInDTO.getPassword(), userAuth.getPassword());
        if (!isMatched) {
            throw new BizException(ErrorCode.PASSWORD_ERROR);
        }

        return userAuth;
    }

}

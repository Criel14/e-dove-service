package com.criel.edove.auth.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.entity.UserAuth;
import com.criel.edove.auth.mapper.UserAuthMapper;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.auth.strategy.PreSignInStrategy;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户登录策略：手机号 + 验证码
 * 若用户不存在，则创建用户
 */
@Component
@RequiredArgsConstructor
public class PhoneOtpPreSignInStrategy implements PreSignInStrategy {

    private final Logger LOGGER = LoggerFactory.getLogger(PhoneOtpPreSignInStrategy.class);

    private final RedissonClient redissonClient;

    private final UserAuthMapper userAuthMapper;

    /**
     * @param signInDTO 调用时保证【手机号】和【验证码】不为空
     * @return 登录的用户认证信息（若用户ID为null，说明需要创建新用户）
     */
    @Override
    public UserAuth preSignIn(SignInDTO signInDTO) {
        LOGGER.info("用户登录策略：手机号（{}） + 验证码", signInDTO.getPhone());

        // 查找用户信息
        String phone = signInDTO.getPhone();
        LambdaQueryWrapper<UserAuth> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(UserAuth::getPhone, phone);
        UserAuth userAuth = userAuthMapper.selectOne(userWrapper);

        // 验证码校验
        String otp = signInDTO.getPhoneOtp();
        String otpKey = RedisKeyConstant.USER_OTP + phone;
        RBucket<String> loginOtp = redissonClient.getBucket(otpKey);
        if (!loginOtp.isExists() || !StrUtil.equals(otp, loginOtp.get())) {
            throw new BizException(ErrorCode.SIGN_IN_PHONE_OTP_ERROR);
        }

        // 验证码校验成功 且 用户不存在，则返回一个空用户ID的用户
        if (userAuth == null) {
            userAuth = new UserAuth();
            userAuth.setPhone(phone);
        }

        return userAuth;
    }

}

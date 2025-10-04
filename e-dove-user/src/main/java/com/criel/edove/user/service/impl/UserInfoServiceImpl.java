package com.criel.edove.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.exception.impl.UpdateInfoEmailAlreadyExistsException;
import com.criel.edove.common.exception.impl.UpdateInfoEmailOtpException;
import com.criel.edove.common.exception.impl.UpdateInfoUsernameAlreadyExistsException;
import com.criel.edove.common.exception.impl.UserInfoMissingUserIdException;
import com.criel.edove.feign.auth.client.AuthFeignClient;
import com.criel.edove.feign.auth.dto.UpdateUserAuthDTO;
import com.criel.edove.user.dto.UpdateUserInfoDTO;
import com.criel.edove.user.dto.UserInfoDTO;
import com.criel.edove.user.entity.UserInfo;
import com.criel.edove.user.mapper.UserInfoMapper;
import com.criel.edove.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.user.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private final UserInfoMapper userInfoMapper;
    private final RedissonClient redissonClient;
    private final AuthFeignClient authFeignClient;

    /**
     * 创建新用户信息
     */
    @Override
    public UserInfoVO createUserInfo(UserInfoDTO userInfoDTO) {
        // 校验是否有用户ID
        if (userInfoDTO.getUserId() == null) {
            throw new UserInfoMissingUserIdException();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userInfoDTO.getUserId());
        // 默认用户名
        if (StrUtil.isEmpty(userInfoDTO.getUsername())) {
            userInfo.setUsername("edove" + userInfoDTO.getUserId());
        } else {
            userInfo.setUsername(userInfoDTO.getUsername());
        }
        if (StrUtil.isNotEmpty(userInfoDTO.getPhone())) {
            userInfo.setPhone(userInfoDTO.getPhone());
        }
        if (StrUtil.isNotEmpty(userInfoDTO.getEmail())) {
            userInfo.setEmail(userInfoDTO.getEmail());
        }
        if (StrUtil.isNotEmpty(userInfoDTO.getAvatarUrl())) {
            userInfo.setAvatarUrl(userInfoDTO.getAvatarUrl());
        }
        userInfoMapper.insert(userInfo);
        return new UserInfoVO(
                userInfo.getUserId(),
                userInfo.getStoreId(),
                userInfo.getUsername(),
                userInfo.getPhone(),
                userInfo.getEmail(),
                userInfo.getAvatarUrl()
        );
    }

    /**
     * 获取当前登录用户的用户信息
     */
    @Override
    public UserInfoVO getUserInfo() {
        Long userId = UserInfoContextHolder.getUserInfoContext().getUserId();
        LambdaQueryWrapper<UserInfo> userInfoWrapper = new LambdaQueryWrapper<>();
        userInfoWrapper.eq(UserInfo::getUserId, userId);
        UserInfo userInfo = userInfoMapper.selectOne(userInfoWrapper);
        return new UserInfoVO(
                userInfo.getUserId(),
                userInfo.getStoreId(),
                userInfo.getUsername(),
                userInfo.getPhone(),
                userInfo.getEmail(),
                userInfo.getAvatarUrl()
        );
    }

    /**
     * 修改用户信息
     *
     * @param updateUserInfoDTO 只允许修改：用户名、邮箱、头像、所属门店
     */
    @Override
    @GlobalTransactional
    public UserInfoVO updateUserInfo(UpdateUserInfoDTO updateUserInfoDTO) {
        UserInfoContext userInfoContext = UserInfoContextHolder.getUserInfoContext();
        Long userId = userInfoContext.getUserId();

        // 检查用户信息是否已存在
        checkUserInfoExist(updateUserInfoDTO, userId);

        // 检查邮箱验证码
        if (StrUtil.isNotEmpty(updateUserInfoDTO.getEmail())) {
            String otpKey = RedisKeyConstant.USER_OTP + updateUserInfoDTO.getEmail();
            RBucket<String> otpBucket = redissonClient.getBucket(otpKey);
            if (!otpBucket.isExists() || !StrUtil.equals(updateUserInfoDTO.getEmailOtp(), otpBucket.get())) {
                throw new UpdateInfoEmailOtpException();
            }
        }

        // 更新用户信息
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setUserId(userId);
        // 更新用户名
        if (StrUtil.isNotEmpty(updateUserInfoDTO.getUsername())) {
            updateUserInfo.setUsername(updateUserInfoDTO.getUsername());
        }
        // 更新邮箱
        if (StrUtil.isNotEmpty(updateUserInfoDTO.getEmail())) {
            updateUserInfo.setEmail(updateUserInfoDTO.getEmail());
        }
        // 更新头像
        if (StrUtil.isNotEmpty(updateUserInfoDTO.getAvatarUrl())) {
            updateUserInfo.setAvatarUrl(updateUserInfoDTO.getAvatarUrl());
        }
        // 更新所属门店
        if (updateUserInfoDTO.getStoreId() != null) {
            updateUserInfo.setStoreId(updateUserInfoDTO.getStoreId());
        }
        userInfoMapper.updateById(updateUserInfo);

        // 查询更新后的数据
        UserInfo userInfo = userInfoMapper.selectById(userId);
        // 更新用户认证信息（需要更新再远程调用）
        if (StrUtil.isNotEmpty(updateUserInfoDTO.getUsername()) || StrUtil.isNotEmpty(updateUserInfoDTO.getEmail())) {
            UpdateUserAuthDTO updateUserAuthDTO = new UpdateUserAuthDTO(userInfo.getUsername(), userInfo.getEmail());
            authFeignClient.update(updateUserAuthDTO);
        }

        return new UserInfoVO(
                userInfo.getUserId(),
                userInfo.getStoreId(),
                userInfo.getUsername(),
                userInfo.getPhone(),
                userInfo.getEmail(),
                userInfo.getAvatarUrl()
        );
    }

    /**
     * 更新用户信息时时：检查用户信息【用户名、邮箱】是否已存在，且不是当前用户
     */
    private void checkUserInfoExist(UpdateUserInfoDTO updateUserInfoDTO, Long userId) {
        LambdaQueryWrapper<UserInfo> userInfoWrapper = new LambdaQueryWrapper<>();
        // 检查用户名和邮箱是否存在，且不是当前用户
        userInfoWrapper
                .ne(UserInfo::getUserId, userId)  // 排除当前用户
                .and(wrapper -> wrapper
                        .eq(UserInfo::getUsername, updateUserInfoDTO.getUsername())  // 用户名匹配
                        .or()
                        .eq(UserInfo::getEmail, updateUserInfoDTO.getEmail())  // 或邮箱匹配
                );
        List<UserInfo> userInfos = userInfoMapper.selectList(userInfoWrapper);
        if (CollUtil.isNotEmpty(userInfos)) {
            for (UserInfo userInfo : userInfos) {
                if (StrUtil.isNotEmpty(userInfo.getUsername())
                        && userInfo.getUsername().equals(updateUserInfoDTO.getUsername())) {
                    throw new UpdateInfoUsernameAlreadyExistsException();
                }
                if (StrUtil.isNotEmpty(userInfo.getEmail())
                        && userInfo.getEmail().equals(updateUserInfoDTO.getEmail())) {
                    throw new UpdateInfoEmailAlreadyExistsException();
                }
            }
        }
    }
}

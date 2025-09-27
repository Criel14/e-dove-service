package com.criel.edove.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.exception.impl.UserInfoMissingUserIdException;
import com.criel.edove.user.dto.UserInfoDTO;
import com.criel.edove.user.entity.UserInfo;
import com.criel.edove.user.mapper.UserInfoMapper;
import com.criel.edove.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.user.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private final UserInfoMapper userInfoMapper;

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
            userInfo.setUsername("dove" + userInfoDTO.getUserId());
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
        return new UserInfoVO(userInfo.getUserId(),
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
        return new UserInfoVO(userInfo.getUserId(),
                userInfo.getUsername(),
                userInfo.getPhone(),
                userInfo.getEmail(),
                userInfo.getAvatarUrl()
        );
    }
}

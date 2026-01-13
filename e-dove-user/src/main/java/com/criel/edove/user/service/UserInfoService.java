package com.criel.edove.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.criel.edove.user.dto.UpdateUserInfoDTO;
import com.criel.edove.user.dto.UserInfoDTO;
import com.criel.edove.user.entity.UserInfo;
import com.criel.edove.user.vo.UserInfoVO;

import java.util.List;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 服务类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfoVO createUserInfo(UserInfoDTO userInfoDTO);

    UserInfoVO getUserInfo();

    UserInfoVO updateUserInfo(UpdateUserInfoDTO updateUserInfoDTO);

    void updateStoreBind(Long storeId);

    Long getUserStoreId();

    List<String> extractPhone(Integer count);
}

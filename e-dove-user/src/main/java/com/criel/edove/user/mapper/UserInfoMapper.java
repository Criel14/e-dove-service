package com.criel.edove.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.user.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}

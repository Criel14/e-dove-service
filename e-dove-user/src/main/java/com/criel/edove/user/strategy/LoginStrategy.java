package com.criel.edove.user.strategy;

import com.criel.edove.common.result.Result;
import com.criel.edove.user.dto.LoginDTO;
import com.criel.edove.user.vo.LoginVO;

/**
 * 用户登录策略接口
 */
public interface LoginStrategy {

    Result<LoginVO> login(LoginDTO loginDTO);

}

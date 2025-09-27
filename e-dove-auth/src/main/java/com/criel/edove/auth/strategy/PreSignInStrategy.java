package com.criel.edove.auth.strategy;

import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.entity.UserAuth;

/**
 * 用户登录前置处理策略接口
 */
public interface PreSignInStrategy {

    /**
     * @param signInDTO 调用时保证响应参数不为空
     * @return 登录的用户认证信息
     */
    UserAuth preSignIn(SignInDTO signInDTO);

}

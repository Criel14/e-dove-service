package com.criel.edove.user.service;

import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.result.Result;
import com.criel.edove.user.vo.TokenRefreshVO;

/**
 * 用户身份验证
 *
 * @author Criel
 * @since 2025-09-22
 */
public interface AuthService {

    Result<TokenRefreshVO> refresh(String refreshToken);

    String createAccessToken(UserInfoContext userInfoContext);

    String createRefreshToken(UserInfoContext userInfoContext);

    void addJwtIdToBlackList(long jwtId);

    UserInfoContext validateAccessToken(String accessToken);

    UserInfoContext validateRefreshToken(String refreshToken);

}

package com.criel.edove.auth.service;

import com.criel.edove.auth.vo.TokenRefreshVO;
import com.criel.edove.common.context.UserInfoContext;

/**
 * token 相关服务
 * @author Criel
 * @since 2025-09-22
 */
public interface TokenService {

    TokenRefreshVO refresh(String refreshToken);

    String createAccessToken(UserInfoContext userInfoContext);

    String createRefreshToken(UserInfoContext userInfoContext);

    void addJwtIdToBlackList(long jwtId);

    UserInfoContext validateAccessToken(String accessToken);

    UserInfoContext validateRefreshToken(String refreshToken);
}

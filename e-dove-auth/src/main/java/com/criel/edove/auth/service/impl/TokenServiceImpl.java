package com.criel.edove.auth.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.criel.edove.auth.properties.JwtProperties;
import com.criel.edove.auth.service.TokenService;
import com.criel.edove.auth.vo.TokenRefreshVO;
import com.criel.edove.common.constant.JWTConstant;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.service.SnowflakeService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户身份验证
 *
 * @author Criel
 * @since 2025-09-22
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final JwtProperties jwtProperties;

    private final RedissonClient redissonClient;

    private final SnowflakeService snowflakeService;


    /**
     * 校验 refresh token 并返回新的 access token 和 refresh token
     *
     * @param refreshToken 用户传来的 refresh token
     */
    @Override
    public TokenRefreshVO refresh(String refreshToken) {
        // 校验1：用户传来的 refresh token 是否在refresh token黑名单中
        // 获取jwt id
        String userJti = getRefreshTokenId(refreshToken);
        String blackListKey = RedisKeyConstant.REFRESH_TOKEN_BLACK_LIST + userJti;
        RBucket<String> blackListBucket = redissonClient.getBucket(blackListKey);
        if (blackListBucket.isExists()) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        // 校验2：用户传来的 refresh token 在redis中是否存在
        // 获取用户信息
        UserInfoContext userInfoContext = validateRefreshToken(refreshToken);
        String refreshTokenKey = RedisKeyConstant.REFRESH_TOKEN_PREFIX + userInfoContext.getUserId();
        RBucket<String> refreshTokenBucket = redissonClient.getBucket(refreshTokenKey);
        if (!refreshTokenBucket.isExists()) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        // 校验3：用户传来的 refresh token 的jti和redis中存储的jti是否一致
        String currentJti = refreshTokenBucket.get();
        if (!currentJti.equals(userJti)) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        // 生成2个新的token
        String newAccessToken = createAccessToken(userInfoContext);
        String newRefreshToken = createRefreshToken(userInfoContext);

        // 返回2个新的token
        return new TokenRefreshVO(newAccessToken, newRefreshToken);
    }

    /**
     * 生成 access token
     *
     * @return 返回生成的 access token
     */
    @Override
    public String createAccessToken(UserInfoContext userInfoContext) {
        // 构建payload
        Map<String, Object> payload = createUserInfoPayload(userInfoContext, Instant.now().plusMillis(jwtProperties.getAccessTtl()));

        // 生成token
        return JWTUtil.createToken(payload, jwtProperties.getAccessKey().getBytes());
    }

    /**
     * 生成 refresh token，并存储到redis中
     *
     * @return 返回生成的 refresh token
     */
    @Override
    public String createRefreshToken(UserInfoContext userInfoContext) {
        // 构建payload
        Map<String, Object> payload = createUserInfoPayload(userInfoContext, Instant.now().plusMillis(jwtProperties.getRefreshTtl()));

        // 生成token
        String token = JWTUtil.createToken(payload, jwtProperties.getRefreshKey().getBytes());

        // 将jwt的id存入redis
        String refreshTokenKey = RedisKeyConstant.REFRESH_TOKEN_PREFIX + userInfoContext.getUserId();
        RBucket<String> refreshTokenBucket = redissonClient.getBucket(refreshTokenKey);
        refreshTokenBucket.set(payload.get(JWTPayload.JWT_ID).toString(), Duration.ofMillis(jwtProperties.getRefreshTtl()));

        // 返回 refresh token
        return token;
    }

    /**
     * 将jwt id存入Redis中的refresh token黑名单；
     * 使用场景：用户下线、强制下线
     */
    @Override
    public void addJwtIdToBlackList(long jwtId) {
        // 用String类型存储，方便快速检索，且可以为每一条id单独设置过期时间
        String blackListKey = RedisKeyConstant.REFRESH_TOKEN_BLACK_LIST + Long.toString(jwtId);
        RBucket<String> blackListBucket = redissonClient.getBucket(blackListKey);
        // 过期时间设置为refresh token的过期时间（这里插入失败没事，说明之前已经加入过黑名单了）
        blackListBucket.setIfAbsent("0", Duration.ofMillis(jwtProperties.getRefreshTtl()));
    }

    /**
     * 解析 access token
     *
     * @return 成功则返回用户信息
     */
    @Override
    public UserInfoContext validateAccessToken(String accessToken) {
        // jwt校验
        boolean validateResult = validateToken(accessToken, jwtProperties.getAccessKey());
        if (!validateResult) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        // 解析token中的用户信息并返回
        JWT jwt = JWTUtil.parseToken(accessToken).setKey(jwtProperties.getAccessKey().getBytes());
        return getUserInfoContext(jwt);
    }

    /**
     * 解析 refresh token
     *
     * @return 成功则返回用户信息
     */
    @Override
    public UserInfoContext validateRefreshToken(String refreshToken) {
        // jwt校验
        boolean validateResult = validateToken(refreshToken, jwtProperties.getRefreshKey());
        if (!validateResult) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        // 解析token中的用户信息并返回
        JWT jwt = JWTUtil.parseToken(refreshToken).setKey(jwtProperties.getRefreshKey().getBytes());
        return getUserInfoContext(jwt);
    }

    /**
     * 校验token
     */
    private boolean validateToken(String token, String key) {
        try {
            JWTValidator.of(token)
                    .validateAlgorithm(JWTSignerUtil.hs256(key.getBytes()))
                    .validateDate(DateUtil.date());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析用户信息
     */
    private UserInfoContext getUserInfoContext(JWT jwt) {
        String userId = jwt.getPayload(JWTConstant.USER_ID).toString();
        String username = jwt.getPayload(JWTConstant.USERNAME).toString();
        String phone = jwt.getPayload(JWTConstant.PHONE).toString();
        return new UserInfoContext(Long.valueOf(userId), username, phone);
    }

    /**
     * 获取refresh token id（String）
     */
    private String getRefreshTokenId(String refreshToken) {
        // jwt校验
        boolean validateResult = validateToken(refreshToken, jwtProperties.getRefreshKey());
        if (!validateResult) {
            throw new BizException(ErrorCode.JWT_ERROR);
        }
        // 获取jwt信息
        JWT jwt = JWTUtil.parseToken(refreshToken).setKey(jwtProperties.getRefreshKey().getBytes());
        return jwt.getPayload(JWTPayload.JWT_ID).toString();
    }

    /**
     * 构建 jwt payload
     */
    private Map<String, Object> createUserInfoPayload(UserInfoContext userInfoContext, Instant expiryInstant) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTConstant.USER_ID, userInfoContext.getUserId()); // 用户ID
        payload.put(JWTConstant.USERNAME, userInfoContext.getUsername()); // 用户名
        payload.put(JWTConstant.PHONE, userInfoContext.getPhone()); // 手机号
        payload.put(JWTPayload.ISSUED_AT, Instant.now()); // 签发时间
        payload.put(JWTPayload.NOT_BEFORE, Instant.now()); // 生效时间
        payload.put(JWTPayload.EXPIRES_AT, expiryInstant); // 过期时间
        payload.put(JWTPayload.JWT_ID, snowflakeService.nextId()); // jwt id
        return payload;
    }
}

package com.criel.edove.auth.strategy.factory;

import com.criel.edove.auth.strategy.PreSignInStrategy;
import com.criel.edove.auth.strategy.impl.EmailPasswordPreSignInStrategy;
import com.criel.edove.auth.strategy.impl.PhoneOtpPreSignInStrategy;
import com.criel.edove.auth.strategy.impl.PhonePasswordPreSignInStrategy;
import com.criel.edove.common.constant.LoginStrategyConstant;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 登录策略Map工厂
 */
@Component
@RequiredArgsConstructor
public class LoginStrategyFactory {

    private Map<String, PreSignInStrategy> strategyMap;

    private final PhoneOtpPreSignInStrategy phoneOtpLoginStrategy;
    private final PhonePasswordPreSignInStrategy phonePasswordLoginStrategy;
    private final EmailPasswordPreSignInStrategy emailPasswordLoginStrategy;

    @PostConstruct
    public void init() {
        strategyMap = Map.of(
                LoginStrategyConstant.PHONE_OTP_LOGIN_STRATEGY, phoneOtpLoginStrategy,
                LoginStrategyConstant.PHONE_PASSWORD_LOGIN_STRATEGY, phonePasswordLoginStrategy,
                LoginStrategyConstant.EMAIL_PASSWORD_LOGIN_STRATEGY, emailPasswordLoginStrategy);
    }

    public PreSignInStrategy getStrategy(String strategyKey) {
        return strategyMap.get(strategyKey);
    }
}

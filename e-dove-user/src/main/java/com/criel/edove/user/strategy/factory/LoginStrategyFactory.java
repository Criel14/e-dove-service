package com.criel.edove.user.strategy.factory;

import com.criel.edove.user.constant.LoginStrategyConstant;
import com.criel.edove.user.strategy.LoginStrategy;
import com.criel.edove.user.strategy.impl.EmailPasswordLoginStrategy;
import com.criel.edove.user.strategy.impl.PhoneOtpLoginStrategy;
import com.criel.edove.user.strategy.impl.PhonePasswordLoginStrategy;
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

    private Map<String, LoginStrategy> strategyMap;

    private final PhoneOtpLoginStrategy phoneOtpLoginStrategy;
    private final PhonePasswordLoginStrategy phonePasswordLoginStrategy;
    private final EmailPasswordLoginStrategy emailPasswordLoginStrategy;

    @PostConstruct
    public void init() {
        strategyMap = Map.of(
                LoginStrategyConstant.PHONE_OTP_LOGIN_STRATEGY, phoneOtpLoginStrategy,
                LoginStrategyConstant.PHONE_PASSWORD_LOGIN_STRATEGY, phonePasswordLoginStrategy,
                LoginStrategyConstant.EMAIL_PASSWORD_LOGIN_STRATEGY, emailPasswordLoginStrategy);
    }

    public LoginStrategy getStrategy(String strategyKey) {
        return strategyMap.get(strategyKey);
    }
}

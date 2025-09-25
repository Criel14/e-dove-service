package com.criel.edove.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * Argon2id 密码哈希算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 构造 Argon2PasswordEncoder（使用官方推荐的配置）
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

}

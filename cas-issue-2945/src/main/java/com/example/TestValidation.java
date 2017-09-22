package com.example;

import java.security.GeneralSecurityException;


import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.pm.PasswordValidationService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

@Primary
@Configuration("testValidation")
@EnableConfigurationProperties
public class TestValidation {

    @RefreshScope
    @Bean
    public PasswordValidationService passwordValidationService() {
        return (c, bean) -> false;
    }

    @RefreshScope
    @Bean
    public PasswordManagementService passwordChangeService() {
        return new PasswordManagementService() {
            public String findEmail(String username) {
                return "test@example.com";
            }
            public String createToken(String username) {
                return username;
            }
            public String parseToken(String token) {
                return token;
            }
        };
    }
}

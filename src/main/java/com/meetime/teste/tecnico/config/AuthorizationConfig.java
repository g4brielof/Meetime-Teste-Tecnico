package com.meetime.teste.tecnico.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class AuthorizationConfig {
    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    @Value("${hubspot.auth-url}")
    private String authUrl;

    @Value("${hubspot.token-url}")
    private String tokenUrl;
}

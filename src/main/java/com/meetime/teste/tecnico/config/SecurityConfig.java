package com.meetime.teste.tecnico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())  // Desativa CSRF para testes locais
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );
        http.headers().frameOptions().disable();
        return http.build();
    }
}

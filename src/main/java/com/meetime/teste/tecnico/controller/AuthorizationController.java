package com.meetime.teste.tecnico.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.meetime.teste.tecnico.config.AuthorizationConfig;
import com.meetime.teste.tecnico.service.AuthorizationService;

@RestController
@RequestMapping("/api/oauth")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationConfig authorizationConfig, AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/authorize")
    public RedirectView authorize() {
        return authorizationService.authorize();
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String authorizationCode) throws Exception {
        authorizationService.callback(authorizationCode);
    }
}

package com.meetime.teste.tecnico.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.meetime.teste.tecnico.config.AuthorizationConfig;

@RestController
@RequestMapping("/api/oauth")
public class AuthorizationController {

    private final AuthorizationConfig authorizationConfig;

    public AuthorizationController(AuthorizationConfig authorizationConfig) {
        this.authorizationConfig = authorizationConfig;
    }

    @GetMapping("/authorize")
    public RedirectView authorize() {
        String AUTH_URL = authorizationConfig.getAuthUrl();
        String CLIENT_ID = authorizationConfig.getClientId();
        String REDIRECT_URI = authorizationConfig.getRedirectUri();

        String REDIRECT_URL = AUTH_URL + 
                                "?client_id=" + CLIENT_ID +
                                "&redirect_uri=" + REDIRECT_URI +
                                "&scope=crm.schemas.contacts.read" +
                                "&response_type=code";

        return new RedirectView(REDIRECT_URL);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String authorizationCode) {
        return "Authorization Code recebido: " + authorizationCode;
    }
}

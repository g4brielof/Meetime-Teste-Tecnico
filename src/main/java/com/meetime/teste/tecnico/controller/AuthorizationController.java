package com.meetime.teste.tecnico.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.meetime.teste.tecnico.config.AuthorizationConfig;
import com.meetime.teste.tecnico.mapper.UserMapper;
import com.meetime.teste.tecnico.mapper.dto.UserDTO;
import com.meetime.teste.tecnico.model.Users;
import com.meetime.teste.tecnico.repository.UsersRepository;

@RestController
@RequestMapping("/api/oauth")
public class AuthorizationController {

    private final String AUTH_URL;
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String REDIRECT_URI;
    private final String TOKEN_URL = "https://api.hubapi.com/oauth/v1/token";

    @Autowired
    private final UsersRepository usersRepository;

    public AuthorizationController(AuthorizationConfig authorizationConfig, UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.AUTH_URL = authorizationConfig.getAuthUrl();
        this.CLIENT_ID = authorizationConfig.getClientId();
        this.CLIENT_SECRET = authorizationConfig.getClientSecret();
        this.REDIRECT_URI = authorizationConfig.getRedirectUri();
    }

    @GetMapping("/authorize")
    public RedirectView authorize() {
        String REDIRECT_URL = AUTH_URL + 
                                "?client_id=" + CLIENT_ID +
                                "&redirect_uri=" + REDIRECT_URI +
                                "&scope=crm.schemas.contacts.read" +
                                "&response_type=code";

        return new RedirectView(REDIRECT_URL);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String authorizationCode) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("redirect_uri", REDIRECT_URI);
        requestBody.add("code", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getBody() != null) {
            UserDTO responseBody = UserMapper.converterJsonParaObjeto(response.getBody());
            String accessToken = responseBody.getAccessToken();
            String refreshToken = responseBody.getRefreshToken();
            Integer expiresIn = responseBody.getExpiresIn();
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expiresIn);

            Users user = new Users(CLIENT_ID, accessToken, refreshToken, expiresAt);
            usersRepository.save(user);
        }

        return ResponseEntity.ok("Response do HubSpot: " + response.getBody());
    }
}

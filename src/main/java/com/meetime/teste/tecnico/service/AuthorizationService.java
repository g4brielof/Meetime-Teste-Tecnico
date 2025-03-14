package com.meetime.teste.tecnico.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.meetime.teste.tecnico.config.AuthorizationConfig;
import com.meetime.teste.tecnico.exception.FailedToReceiveAccessTokenException;
import com.meetime.teste.tecnico.exception.FailedToRefreshAccessTokenException;
import com.meetime.teste.tecnico.mapper.UserMapper;
import com.meetime.teste.tecnico.mapper.dto.UserDTO;
import com.meetime.teste.tecnico.model.Users;
import com.meetime.teste.tecnico.repository.UsersRepository;

@Service
public class AuthorizationService {
    private final String AUTH_URL;
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String REDIRECT_URI;
    private final String TOKEN_URL = "https://api.hubapi.com/oauth/v1/token";

    private final UsersRepository usersRepository;

   public AuthorizationService(AuthorizationConfig authorizationConfig, UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.AUTH_URL = authorizationConfig.getAuthUrl();
        this.CLIENT_ID = authorizationConfig.getClientId();
        this.CLIENT_SECRET = authorizationConfig.getClientSecret();
        this.REDIRECT_URI = authorizationConfig.getRedirectUri();
    }

    public RedirectView authorize() {
        System.out.println("STARTING REDIRECT!");
        String REDIRECT_URL = AUTH_URL + 
                                "?client_id=" + 
                                CLIENT_ID +
                                "&redirect_uri=" + 
                                REDIRECT_URI +
                                "&scope=crm.objects.contacts.write%20crm.objects.contacts.read" +
                                "&response_type=code";

        return new RedirectView(REDIRECT_URL);
    }

    public ResponseEntity<Map<String, Object>> callback(String authorizationCode) throws Exception {
        System.out.println("AUTHORIZATION TOKEN RECEIVED!");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("redirect_uri", REDIRECT_URI);
        requestBody.add("code", authorizationCode);

        ResponseEntity<String> response = requestToken(requestBody);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            System.out.println("ACCESS TOKEN RECEIVED!");
            UserDTO responseBody = UserMapper.usersConverter(response.getBody());

            String accessToken = responseBody.getAccessToken();
            String refreshToken = responseBody.getRefreshToken();
            Integer expiresIn = responseBody.getExpiresIn();
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expiresIn);

            Users user = new Users(CLIENT_ID, accessToken, refreshToken, expiresAt);
            usersRepository.save(user);

            return buildResponse("ACCESS TOKEN RECEIVED!", HttpStatus.OK);
        }
        
        System.out.println("FAILED TO RECEIVE ACCESS TOKEN!");
        throw new FailedToReceiveAccessTokenException("FAILED TO RECEIVE ACCESS TOKEN!");
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        System.out.println("TRYING TO GET A NEW TOKEN!");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "refresh_token");
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("refresh_token", refreshToken);

        ResponseEntity<String> response = requestToken(body);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            UserDTO responseBody = UserMapper.usersConverter(response.getBody());

            String newAccessToken = responseBody.getAccessToken();
            String newRefreshToken = responseBody.getRefreshToken();

            Optional<Users> token = usersRepository.findTopByClientIdOrderByExpiresAtDesc(CLIENT_ID);
            Users userToken = token.get();
            userToken.setAccessToken(newAccessToken);
            userToken.setRefreshToken(newRefreshToken);
            
            usersRepository.save(userToken);
            System.out.println("NEW ACCESS TOKEN RECEIVED!");
            return newAccessToken;
        }

        throw new FailedToRefreshAccessTokenException("FAILED TO REFRESH ACCESS TOKEN!");
    }

    private ResponseEntity<String> requestToken(MultiValueMap<String, String> requestBody) {
        System.out.println("REQUESTING ACCESS TOKEN!");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
            TOKEN_URL, 
            HttpMethod.POST, 
            request, 
            String.class);
    }

    private static <T> ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }
}

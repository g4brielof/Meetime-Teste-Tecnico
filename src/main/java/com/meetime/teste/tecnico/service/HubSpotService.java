package com.meetime.teste.tecnico.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.meetime.teste.tecnico.config.AuthorizationConfig;
import com.meetime.teste.tecnico.exception.UserWithValidClientIdNotFoundException;
import com.meetime.teste.tecnico.mapper.ContactEventsMapper;
import com.meetime.teste.tecnico.mapper.dto.ContactEventsDTO;
import com.meetime.teste.tecnico.model.ContactEvents;
import com.meetime.teste.tecnico.model.Users;
import com.meetime.teste.tecnico.repository.ContactEventsRepository;
import com.meetime.teste.tecnico.repository.UsersRepository;

@Service
public class HubSpotService {
    private final String TOKEN_URL = "https://api.hubapi.com/oauth/v1/token";
    private final String HUBSPOT_CREATE_CONTACT_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    private final UsersRepository usersRepository;
    private final ContactEventsRepository contactEventsRepository;
    private final AuthorizationConfig authorizationConfig;
    private final AuthorizationService authorizationService;

    public HubSpotService(AuthorizationConfig authorizationConfig,
                            UsersRepository usersRepository,
                            ContactEventsRepository contactEventsRepository,
                            AuthorizationService authorizationService) {

        this.authorizationConfig = authorizationConfig;
        this.usersRepository = usersRepository;
        this.contactEventsRepository = contactEventsRepository;
        this.authorizationService = authorizationService;
    }

    public ResponseEntity<String> createContact(String email, String firstName, String lastName, String phone) throws Exception {
        System.out.println("CREATING A NEW CONTACT!");
        RestTemplate restTemplate = new RestTemplate();
        String clientId = authorizationConfig.getClientId();

        Optional<Users> userToken = usersRepository.findTopByClientIdOrderByExpiresAtDesc(clientId);

        if (userToken.isEmpty()) {
            throw new UserWithValidClientIdNotFoundException("INVALID USER!");
        }

        Users token = userToken.get();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.getAccessToken());
        headers.set("Content-Type", "application/json");

        Map<String, Object> properties = new HashMap<>();
        properties.put("email", email);
        properties.put("firstname", firstName);
        properties.put("lastname", lastName);
        properties.put("phone", phone);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", properties);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            HUBSPOT_CREATE_CONTACT_URL,
            HttpMethod.POST,
            request,
            String.class);
        
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            System.out.println("TOKEN EXPIRED!");
            String novoToken = authorizationService.refreshAccessToken(token.getRefreshToken());
            headers.setBearerAuth(novoToken);
            request = new HttpEntity<>(headers);
    
            response = restTemplate.exchange(TOKEN_URL, HttpMethod.GET, request, String.class);
        }
        System.out.println("NEW CONTACT CREATED!");
        return response;
    }

    public void processWebHookEvents(String payload) throws Exception {
        ContactEvents contactEvents = new ContactEvents();
        ContactEventsDTO contactEventPayload = ContactEventsMapper.contactEventsConverter(payload);
        
        contactEvents.setEventId(contactEventPayload.getEventId());
        contactEvents.setSubscriptionId(contactEventPayload.getSubscriptionId());
        contactEvents.setPortalId(contactEventPayload.getPortalId());
        contactEvents.setAppId(contactEventPayload.getAppId());
        contactEvents.setOccurredAt(contactEventPayload.getOccurredAt());
        contactEvents.setSubscriptionType(contactEventPayload.getSubscriptionType());
        contactEvents.setAttemptNumber(contactEventPayload.getAttemptNumber());
        contactEvents.setObjectId(contactEventPayload.getObjectId());
        contactEvents.setChangeFlag(contactEventPayload.getChangeFlag());
        contactEvents.setChangeSource(contactEventPayload.getChangeSource());
        contactEvents.setSourceId(contactEventPayload.getSourceId());
        
        contactEventsRepository.save(contactEvents);
        System.out.println("NEW EVENT WITH ID " + contactEventPayload.getEventId() + " SAVED ON DATABASE!");
    }
}

package com.meetime.teste.tecnico.service;

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
import com.meetime.teste.tecnico.mapper.ContactEventsMapper;
import com.meetime.teste.tecnico.mapper.dto.ContactEventsDTO;
import com.meetime.teste.tecnico.model.ContactEvents;
import com.meetime.teste.tecnico.model.Users;
import com.meetime.teste.tecnico.repository.ContactEventsRepository;
import com.meetime.teste.tecnico.repository.UsersRepository;

@Service
public class HubSpotService {
    
    private final RestTemplate restTemplate;
    private final String HUBSPOT_CREATE_CONTACT_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    private final UsersRepository usersRepository;
    private final ContactEventsRepository contactEventsRepository;
    private final AuthorizationConfig authorizationConfig;

    public HubSpotService(AuthorizationConfig authorizationConfig, UsersRepository usersRepository, ContactEventsRepository contactEventsRepository) {
        this.authorizationConfig = authorizationConfig;
        this.usersRepository = usersRepository;
        this.contactEventsRepository = contactEventsRepository;
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> createContact(String email, String firstName, String lastName, String phone) {
        String clientId = authorizationConfig.getClientId();

        if (clientId == null || clientId.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CLIENT_ID não configurado nas variáveis de ambiente.");
        }

        Optional<Users> userToken = usersRepository.findTopByClientIdOrderByExpiresAtDesc(clientId);

        if (userToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não encontrado para o Client ID: " + clientId);
        }

        Users token = userToken.get();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.getAccessToken());
        headers.set("Content-Type", "application/json");

        // Criando o corpo da requisição com os dados do contato
        Map<String, Object> properties = new HashMap<>();
        properties.put("email", email);
        properties.put("firstname", firstName);
        properties.put("lastname", lastName);
        properties.put("phone", phone);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", properties);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(HUBSPOT_CREATE_CONTACT_URL, HttpMethod.POST, entity, String.class);
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

        System.out.println(contactEvents.toString());

        contactEventsRepository.save(contactEvents);
    }
}

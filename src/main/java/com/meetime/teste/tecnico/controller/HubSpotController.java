package com.meetime.teste.tecnico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meetime.teste.tecnico.dto.CreateContactDTO;
import com.meetime.teste.tecnico.service.HubSpotService;

@RestController
@RequestMapping("/api/hubspot")
public class HubSpotController {
    private final HubSpotService hubSpotService;

    public HubSpotController(HubSpotService hubSpotService) {
        this.hubSpotService = hubSpotService;
    }

    @PostMapping("/new-contact")
    public ResponseEntity<String> createContact(@RequestBody CreateContactDTO createContactDTO) {
        return hubSpotService.createContact(
            createContactDTO.getEmail(),
            createContactDTO.getFirstName(),
            createContactDTO.getLastName(),
            createContactDTO.getPhone()
        );
    }
}

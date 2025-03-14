package com.meetime.teste.tecnico.mapper;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.teste.tecnico.mapper.dto.ContactEventsDTO;

public class ContactEventsMapper {
        private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ContactEventsDTO contactEventsConverter(String json) throws Exception {
        List<ContactEventsDTO> payload = objectMapper.readValue(json, new TypeReference<List<ContactEventsDTO>>() {});
        return payload.get(0);
    }
}

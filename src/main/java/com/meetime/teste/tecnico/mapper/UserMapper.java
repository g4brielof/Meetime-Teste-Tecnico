package com.meetime.teste.tecnico.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.teste.tecnico.mapper.dto.UserDTO;

public class UserMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static UserDTO usersConverter(String json) throws Exception {
        return objectMapper.readValue(json, UserDTO.class);
    }
}

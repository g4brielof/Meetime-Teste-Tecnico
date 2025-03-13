package com.meetime.teste.tecnico.dto;

import lombok.Data;

@Data
public class CreateContactDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}

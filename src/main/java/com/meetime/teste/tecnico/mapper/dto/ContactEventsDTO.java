package com.meetime.teste.tecnico.mapper.dto;

import java.math.BigInteger;

import lombok.Data;

@Data
public class ContactEventsDTO {
    private Long eventId;
    private Long subscriptionId;
    private Long portalId;
    private Long appId;
    private Long occurredAt;
    private String subscriptionType;
    private Long attemptNumber;
    private Long objectId;
    private String changeFlag;
    private String changeSource;
    private String sourceId;
}
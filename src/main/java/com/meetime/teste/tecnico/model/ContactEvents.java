package com.meetime.teste.tecnico.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_events_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactEvents {

    @Id
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

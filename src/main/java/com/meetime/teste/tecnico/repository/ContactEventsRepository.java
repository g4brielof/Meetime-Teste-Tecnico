package com.meetime.teste.tecnico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meetime.teste.tecnico.model.ContactEvents;

@Repository
public interface ContactEventsRepository extends JpaRepository<ContactEvents, Integer> {
}

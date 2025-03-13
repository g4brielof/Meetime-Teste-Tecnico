package com.meetime.teste.tecnico.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meetime.teste.tecnico.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
}

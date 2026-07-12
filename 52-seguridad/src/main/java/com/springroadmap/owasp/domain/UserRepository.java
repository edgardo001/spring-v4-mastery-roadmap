package com.springroadmap.owasp.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // BUENO: método derivado → Spring genera consulta parametrizada (PreparedStatement).
    List<User> findByEmail(String email);
}

package com.springroadmap.migrations.repository;

import com.springroadmap.migrations.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}

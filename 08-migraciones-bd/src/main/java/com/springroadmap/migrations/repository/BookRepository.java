package com.springroadmap.migrations.repository;

import com.springroadmap.migrations.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

package com.springroadmap.owasp.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.owasp.domain.User;
import com.springroadmap.owasp.domain.UserRepository;

@RestController
@RequestMapping("/api/users")
public class SearchController {

    private final UserRepository userRepository;

    public SearchController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * MALO — SQL Injection: NUNCA hacer esto.
     *
     * @Query(value = "SELECT * FROM users WHERE email = '" + email + "'", nativeQuery = true)
     * List<User> unsafeSearch(String email);
     *
     * Si email == "' OR '1'='1"  →  devuelve TODA la tabla.
     */
    @GetMapping("/search")
    public List<User> search(@RequestParam String email) {
        // BUENO: query derivada / parametrizada → los valores viajan como bind params.
        return userRepository.findByEmail(email);
    }
}

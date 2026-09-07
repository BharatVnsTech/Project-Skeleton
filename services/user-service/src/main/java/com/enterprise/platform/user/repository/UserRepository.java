package com.enterprise.platform.user.repository;

import com.enterprise.platform.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final List<User> USERS = List.of(
            User.builder().id(101L).name("John Doe").email("john@example.com").build(),
            User.builder().id(102L).name("Jane Smith").email("jane@example.com").build(),
            User.builder().id(103L).name("Bob Wilson").email("bob@example.com").build(),
            User.builder().id(104L).name("Alice Brown").email("alice@example.com").build(),
            User.builder().id(105L).name("Charlie Davis").email("charlie@example.com").build()
    );

    public List<User> findAll() {
        return USERS;
    }

    public Optional<User> findById(Long id) {
        return USERS.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
}

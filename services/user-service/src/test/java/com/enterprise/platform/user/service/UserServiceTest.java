package com.enterprise.platform.user.service;

import com.enterprise.platform.exception.ResourceNotFoundException;
import com.enterprise.platform.user.model.User;
import com.enterprise.platform.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user1 = User.builder().id(101L).name("John").email("john@example.com").build();
        User user2 = User.builder().id(102L).name("Jane").email("jane@example.com").build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = User.builder().id(101L).name("John").email("john@example.com").build();
        when(userRepository.findById(101L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(101L);

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("John", result.getName());
    }

    @Test
    void getUserById_shouldThrowWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }
}

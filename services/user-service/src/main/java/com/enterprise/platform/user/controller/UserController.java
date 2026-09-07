package com.enterprise.platform.user.controller;

import com.enterprise.platform.core.CorrelationConstants;
import com.enterprise.platform.logging.CorrelationIdContext;
import com.enterprise.platform.user.model.User;
import com.enterprise.platform.user.service.UserService;
import com.enterprise.platform.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        log.info("GET /api/v1/users - correlationId={}", correlationId);

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully", correlationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        log.info("GET /api/v1/users/{} - correlationId={}", id, correlationId);

        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully", correlationId));
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CorrelationConstants.CORRELATION_ID_HEADER);
        return CorrelationIdContext.getOrCreate(correlationId);
    }
}

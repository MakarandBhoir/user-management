package com.demo.usermanagement.controller;

import com.demo.usermanagement.model.User;
import com.demo.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// TODO: Add proper input validation with @Valid and Bean Validation
// TODO: Add global exception handler (@ControllerAdvice)
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "CRUD operations for users")
@CrossOrigin(origins = "*") // TODO: Restrict CORS in production
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // TECHNICAL DEBT: Field injection instead of constructor injection
    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("POST /users - Creating user: {}", user.getEmail());
        // TODO: No exception handling - any error results in 500
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("GET /users - Listing all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("GET /users/{} - Fetching user", id);
        Optional<User> user = userService.getUserById(id);
        // TODO: Return 404 with proper error body, not just status
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user by ID")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("PUT /users/{} - Updating user", id);
        // TODO: No exception handling - RuntimeException from service results in 500
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

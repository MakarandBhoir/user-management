package com.demo.usermanagement.controller;

import com.demo.usermanagement.model.User;
import com.demo.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController - Intentionally vulnerable endpoints for security demo
 * purposes.
 *
 * VULNERABILITIES PRESENT (by design for demo):
 * 1. SQL Injection via /admin/users/search
 * 2. XSS via /admin/greet
 * 3. Sensitive data exposure via /admin/users/dump
 * 4. Slow response endpoint at /admin/users/slow
 * 5. No authorization check on any endpoint
 */
// TODO: Secure all admin endpoints with ROLE_ADMIN check
// TODO: Add request rate limiting
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin (Vulnerable Demo)", description = "Intentionally vulnerable admin endpoints for demo purposes")
@CrossOrigin(origins = "*") // TODO: Restrict CORS in production
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // TECHNICAL DEBT: Field injection
    @Autowired
    private UserService userService;

    /**
     * VULNERABILITY: SQL Injection
     * The `name` query parameter is passed directly into a raw SQL string without
     * sanitization.
     * Attacker input example: ?name=' OR '1'='1
     * This will return all users in the database.
     * TODO: Use parameterized queries or Spring Data method queries
     */
    @Operation(summary = "[VULNERABLE] Search users by name (SQL Injection demo)", description = "INTENTIONAL SQL INJECTION: name param is concatenated directly into SQL query. "
            +
            "Try: ?name=' OR '1'='1")
    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        logger.warn("SQL Injection vulnerable endpoint called with name: {}", name);
        // VULNERABILITY: Raw SQL string concatenation in service layer
        List<User> results = userService.searchUsersByName(name);
        return ResponseEntity.ok(results);
    }

    /**
     * VULNERABILITY: Cross-Site Scripting (XSS)
     * The `message` parameter is reflected directly into the HTML response
     * without any HTML encoding or sanitization.
     * Attacker input example: ?message=<script>alert('XSS')</script>
     * TODO: Encode output using HtmlUtils.htmlEscape() or return JSON instead of
     * HTML
     */
    @Operation(summary = "[VULNERABLE] Greet endpoint (XSS demo)", description = "INTENTIONAL XSS: message param is reflected directly into HTML. "
            +
            "Try: ?message=<script>alert('XSS')</script>")
    @GetMapping(value = "/greet", produces = "text/html")
    public String greet(@RequestParam(defaultValue = "World") String message) {
        logger.warn("XSS vulnerable endpoint called with message: {}", message);
        // VULNERABILITY: Unsanitized user input reflected in HTML response
        return "<html><body><h1>Hello, " + message + "!</h1></body></html>";
    }

    /**
     * VULNERABILITY: Sensitive Data Exposure
     * Returns all user records including plain-text passwords with no
     * authentication required.
     * TODO: Require ROLE_ADMIN, exclude password field, add audit logging
     */
    @Operation(summary = "[VULNERABLE] Dump all users with passwords (Sensitive Data Exposure demo)", description = "INTENTIONAL SENSITIVE DATA EXPOSURE: Returns all users including plain-text passwords. No auth required.")
    @GetMapping("/users/dump")
    public ResponseEntity<List<User>> dumpAllUsers() {
        logger.warn("SENSITIVE DATA DUMP endpoint called - returning all users with passwords!");
        // VULNERABILITY: Returns password field in plain text, accessible without any
        // auth
        return ResponseEntity.ok(userService.getAllUsersWithSensitiveData());
    }

    /**
     * Slow response endpoint to simulate heavy processing.
     * Useful for monitoring/alerting demos (e.g., latency alerts in Azure Monitor).
     * TECHNICAL DEBT: Hardcoded 5-second sleep
     */
    @Operation(summary = "Slow response endpoint (monitoring demo)", description = "Simulates a 5-second slow operation. Useful for latency alerting demos.")
    @GetMapping("/users/slow")
    public ResponseEntity<Map<String, Object>> slowEndpoint() {
        logger.info("Slow endpoint called - will sleep for 5 seconds");
        List<User> users = userService.getAllUsersSlowly();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Slow operation completed");
        response.put("userCount", users.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Returns application info including hardcoded admin credentials.
     * VULNERABILITY: Hardcoded credentials exposed via API response.
     * TODO: Remove this endpoint entirely, never expose credentials via API
     */
    @Operation(summary = "[VULNERABLE] App info with hardcoded credentials (demo)", description = "INTENTIONAL: Returns hardcoded admin credentials in the response.")
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> appInfo() {
        logger.warn("App info endpoint called - exposing hardcoded credentials!");
        Map<String, String> info = new HashMap<>();
        info.put("application", "User Management System");
        info.put("version", "1.0.0");
        // VULNERABILITY: Hardcoded credentials exposed in API response
        info.put("default_admin_user", "admin");
        info.put("default_admin_password", "admin123"); // HARDCODED CREDENTIAL
        info.put("db_url", "jdbc:h2:mem:userdb");
        info.put("note", "DEMO ONLY - These credentials are intentionally exposed for security training");
        return ResponseEntity.ok(info);
    }
}

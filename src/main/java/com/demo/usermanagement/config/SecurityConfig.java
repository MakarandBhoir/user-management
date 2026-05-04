package com.demo.usermanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SecurityConfig - Intentionally misconfigured for demo/training purposes.
 *
 * VULNERABILITIES (intentional):
 * 1. Hardcoded credentials (admin/admin123, user/password)
 * 2. Passwords stored without encoding (plain text via {noop})
 * 3. CSRF protection disabled
 * 4. All endpoints permitted without authorization
 * 5. H2 console exposed with frameOptions disabled
 * 6. No HTTPS enforcement
 *
 * TODO: Use BCryptPasswordEncoder for all passwords
 * TODO: Enable CSRF or use stateless JWT auth
 * TODO: Restrict endpoints to appropriate roles
 * TODO: Disable H2 console in production
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.warn("INSECURE SecurityConfig loaded - CSRF disabled, all endpoints open. FOR DEMO ONLY.");

        http
                // VULNERABILITY: CSRF protection disabled
                // TODO: Enable CSRF for state-changing operations
                .csrf(AbstractHttpConfigurer::disable)

                // VULNERABILITY: All requests are permitted without any authentication or
                // authorization
                // TODO: Restrict /admin/** to ROLE_ADMIN, /users/** to authenticated users
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())

                // Basic auth enabled but not enforced due to permitAll above
                .httpBasic(withDefaults())

                // VULNERABILITY: Frame options disabled to allow H2 console in iframes
                // TODO: Remove this in production - enables clickjacking
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    /**
     * VULNERABILITY: Hardcoded credentials with no password encoding.
     * Using {noop} prefix means passwords are stored and compared in plain text.
     * TODO: Use BCryptPasswordEncoder and store users in the database
     * TODO: Rotate credentials and load from environment variables or secret
     * manager
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // HARDCODED CREDENTIAL - admin/admin123
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123") // VULNERABILITY: Plain text password
                .roles("ADMIN", "USER")
                .build();

        // HARDCODED CREDENTIAL - user/password
        UserDetails user = User.withUsername("user")
                .password("{noop}password") // VULNERABILITY: Plain text password
                .roles("USER")
                .build();

        logger.warn("Loaded hardcoded in-memory users: admin/admin123, user/password. FOR DEMO ONLY.");
        return new InMemoryUserDetailsManager(admin, user);
    }
}

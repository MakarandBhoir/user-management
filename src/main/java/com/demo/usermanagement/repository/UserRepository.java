package com.demo.usermanagement.repository;

import com.demo.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    // VULNERABILITY: Native query with string interpolation used in service layer
    // This method is safe but the service calls nativeQuery with raw input
    @Query(value = "SELECT * FROM users WHERE name = ?1", nativeQuery = true)
    List<User> findByNameNative(String name);
}

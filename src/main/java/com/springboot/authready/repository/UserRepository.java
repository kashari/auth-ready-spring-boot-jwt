package com.springboot.authready.repository;

import com.springboot.authready.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    User findByResetPasswordToken(String token);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

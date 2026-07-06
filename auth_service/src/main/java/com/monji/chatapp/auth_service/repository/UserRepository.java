package com.monji.chatapp.auth_service.repository;

import com.monji.chatapp.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhoneOrUsername(String email, String phone, String username);
}

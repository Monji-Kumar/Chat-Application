package com.monji.chatapp.user_service.repository;

import com.monji.chatapp.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsernameOrEmail(String username, String email);
    boolean  existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserProfile> findByAuthUserId(String authUserId);
    List<UserProfile> findByDisplayNameContainingIgnoreCase(String displayName);
}

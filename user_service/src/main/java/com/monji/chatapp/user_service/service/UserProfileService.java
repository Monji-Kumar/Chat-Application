package com.monji.chatapp.user_service.service;

import com.monji.chatapp.user_service.dto.CreateUserProfileRequest;
import com.monji.chatapp.user_service.dto.UserProfileResponse;
import com.monji.chatapp.user_service.dto.ValidateRegistrationRequest;
import com.monji.chatapp.user_service.dto.ValidateRegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserProfileService {
    ValidateRegistrationResponse validateRegistration(ValidateRegistrationRequest requestDto);

    UserProfileResponse createProfile(CreateUserProfileRequest requestDto);

    UserProfileResponse getUserProfile(String authUserId);

    List<UserProfileResponse> getSearchProfile(String query);

    UserProfileResponse getLoggedInUserProfile(HttpServletRequest request, HttpServletResponse response);
}

package com.monji.chatapp.user_service.controller;

import com.monji.chatapp.common.response.ApiResponse;
import com.monji.chatapp.user_service.dto.CreateUserProfileRequest;
import com.monji.chatapp.user_service.dto.UserProfileResponse;
import com.monji.chatapp.user_service.dto.ValidateRegistrationRequest;
import com.monji.chatapp.user_service.dto.ValidateRegistrationResponse;
import com.monji.chatapp.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserProfileService userProfileService;

    @PostMapping(path = "/validate-registration")
    public ResponseEntity<?> validateRegistration(@Valid @RequestBody ValidateRegistrationRequest requestDto) {
        ValidateRegistrationResponse responseDto = userProfileService.validateRegistration(requestDto);
        return ResponseEntity.ok(ApiResponse.success("Registration Request successfully validated", responseDto));
    }

    @PostMapping(path = "/create-profile")
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateUserProfileRequest requestDto) {
        UserProfileResponse responseDto = userProfileService.createProfile(requestDto);
        return ResponseEntity.ok(ApiResponse.success("User profile created successfully", responseDto));
    }
}

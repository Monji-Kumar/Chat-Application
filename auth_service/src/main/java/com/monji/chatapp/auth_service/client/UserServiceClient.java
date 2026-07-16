package com.monji.chatapp.auth_service.client;

import com.monji.chatapp.auth_service.dto.CreateUserProfileRequest;
import com.monji.chatapp.auth_service.dto.UserProfileResponse;
import com.monji.chatapp.auth_service.dto.ValidateRegistrationRequest;
import com.monji.chatapp.auth_service.dto.ValidateRegistrationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/users/internal")
public interface UserServiceClient {

    @PostMapping(path = "/validate-registration")
    ValidateRegistrationResponse validateRegistration(@RequestBody ValidateRegistrationRequest request);

    @PostMapping("/create-profile")
    UserProfileResponse createProfile(@RequestBody CreateUserProfileRequest request);
}

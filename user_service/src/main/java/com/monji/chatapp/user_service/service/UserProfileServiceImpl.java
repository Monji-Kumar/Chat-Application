package com.monji.chatapp.user_service.service;

import com.monji.chatapp.common.security.JwtService;
import com.monji.chatapp.user_service.dto.CreateUserProfileRequest;
import com.monji.chatapp.user_service.dto.UserProfileResponse;
import com.monji.chatapp.user_service.dto.ValidateRegistrationRequest;
import com.monji.chatapp.user_service.dto.ValidateRegistrationResponse;
import com.monji.chatapp.user_service.entity.UserProfile;
import com.monji.chatapp.user_service.repository.UserProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    @Override
    public ValidateRegistrationResponse validateRegistration(ValidateRegistrationRequest requestDto) {
        ValidateRegistrationResponse response = null;
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUsernameOrEmail(requestDto.getUsername(), requestDto.getEmail());
        if (!userProfileOpt.isPresent()) {
            response = new ValidateRegistrationResponse();
            response.setValid(true);
            response.setEmailAvailable(true);
            response.setUsernameAvailable(true);
            response.setMessage("Valid Registration");
        } else {
            boolean emailExists = userProfileRepository.existsByEmail(requestDto.getEmail());
            boolean usernameExists = userProfileRepository.existsByUsername(requestDto.getUsername());
            response = new ValidateRegistrationResponse();
            response.setValid(false);
            response.setEmailAvailable(!emailExists);
            response.setUsernameAvailable(!usernameExists);
            StringBuilder message = new StringBuilder();
            if(emailExists &&  usernameExists) {
                message.append("Email and username already exists");
            } else if (emailExists) {
                message.append("Email already exists");
            } else {
                message.append("Username already exists");
            }
            response.setMessage(message.toString());
        }
        return response;
    }

    @Override
    public UserProfileResponse createProfile(CreateUserProfileRequest requestDto) {
        UserProfile profile = modelMapper.map(requestDto, UserProfile.class);
        profile = userProfileRepository.save(profile);
        return modelMapper.map(profile, UserProfileResponse.class);
    }

    @Override
    public UserProfileResponse getUserProfile(String authUserId) {
        Optional<UserProfile> userProfile = userProfileRepository.findByAuthUserId(authUserId);
        if (!userProfile.isPresent()) {
            return new UserProfileResponse();
        } else {
            return modelMapper.map(userProfile.get(), UserProfileResponse.class);
        }
    }

    @Override
    public List<UserProfileResponse> getSearchProfile(String query) {
        if(query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserProfile> userProfiles = userProfileRepository.findByDisplayNameContainingIgnoreCase(query);
        List<UserProfileResponse> userProfileResponseList = new ArrayList<>();
        userProfiles.forEach(userProfile -> {userProfileResponseList.add(modelMapper.map(userProfile, UserProfileResponse.class));});
        return userProfileResponseList;
    }

    @Override
    public UserProfileResponse getLoggedInUserProfile(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        } else {
            String token = authorization.substring(7);
            Long authUserId = jwtService.extractUserId(token);
            return getUserProfile(String.valueOf(authUserId));
        }
    }
}

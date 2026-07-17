package com.monji.chatapp.user_service.service;

import com.monji.chatapp.user_service.dto.CreateUserProfileRequest;
import com.monji.chatapp.user_service.dto.UserProfileResponse;
import com.monji.chatapp.user_service.dto.ValidateRegistrationRequest;
import com.monji.chatapp.user_service.dto.ValidateRegistrationResponse;
import com.monji.chatapp.user_service.entity.UserProfile;
import com.monji.chatapp.user_service.exceptions.UnauthorizedUserException;
import com.monji.chatapp.user_service.exceptions.UserProfileNotFoundException;
import com.monji.chatapp.user_service.repository.UserProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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

    @Override
    public ValidateRegistrationResponse validateRegistration(ValidateRegistrationRequest requestDto) {
        ValidateRegistrationResponse response = null;
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUsernameOrEmail(requestDto.getUsername(), requestDto.getEmail());
        if (userProfileOpt.isEmpty()) {
            response = ValidateRegistrationResponse.builder()
                    .valid(true)
                    .emailAvailable(true)
                    .usernameAvailable(true)
                    .message("Valid Registration")
                    .build();
        } else {
            boolean emailExists = userProfileRepository.existsByEmail(requestDto.getEmail());
            boolean usernameExists = userProfileRepository.existsByUsername(requestDto.getUsername());
            StringBuilder message = new StringBuilder();
            if(emailExists &&  usernameExists) {
                message.append("Email and username already exists");
            } else if (emailExists) {
                message.append("Email already exists");
            } else {
                message.append("Username already exists");
            }
            response = ValidateRegistrationResponse.builder()
                    .valid(false)
                    .emailAvailable(!emailExists)
                    .usernameAvailable(!usernameExists)
                    .message(message.toString())
                    .build();
        }
        return response;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public UserProfileResponse createProfile(CreateUserProfileRequest requestDto) {
        UserProfile profile = UserProfile.builder()
                .authUserId(requestDto.getAuthUserId())
                .username(requestDto.getUsername())
                .displayName(requestDto.getDisplayName())
                .avatarUrl(requestDto.getAvatarUrl())
                .email(requestDto.getEmail())
                .statusMessage(requestDto.getStatusMessage())
                .build();
        profile = userProfileRepository.save(profile);
        return modelMapper.map(profile, UserProfileResponse.class);
    }

    @Override
    public UserProfileResponse getUserProfile(String authUserId) {
        Optional<UserProfile> userProfile = userProfileRepository.findByAuthUserId(authUserId);
        if (userProfile.isEmpty()) {
            throw new UserProfileNotFoundException("No User Profile found!");
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
        String authUserId = request.getHeader("X-User-Id");
        if(authUserId == null || authUserId.isBlank()) {
           throw new UnauthorizedUserException("Authentication Failed flor logged In user");
        }

        return getUserProfile(authUserId);
    }
}

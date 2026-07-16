package com.monji.chatapp.user_service.controller;

import com.monji.chatapp.user_service.dto.UserProfileResponse;
import com.monji.chatapp.user_service.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping(path = "/me")
    public ResponseEntity<?> getMe(HttpServletRequest request, HttpServletResponse response) {
        UserProfileResponse responseDto = userProfileService.getLoggedInUserProfile(request, response);
        if(responseDto == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)  .body("Missing or Invalid User Identity");
        }
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(path = "/{authUserId}")
    public ResponseEntity<?> getUserProfile(@PathVariable(name = "authUserId", required = true) String authUserId) {
        UserProfileResponse responseDto =  userProfileService.getUserProfile(authUserId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchProfile(@RequestParam(name = "query", defaultValue = "") String query) {
        List<UserProfileResponse> responseDto =  userProfileService.getSearchProfile(query);
        return ResponseEntity.ok(responseDto);
    }
}

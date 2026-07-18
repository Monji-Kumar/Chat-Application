package com.monji.chatapp.auth_service.controller;

import com.monji.chatapp.auth_service.dto.LoginRequestDto;
import com.monji.chatapp.auth_service.dto.LoginResponseDto;
import com.monji.chatapp.auth_service.dto.RegisterRequestDto;
import com.monji.chatapp.auth_service.service.AuthService;
import com.monji.chatapp.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequestDto)
    {
        authService.registerUser(registerRequestDto);
        return ResponseEntity.ok(ApiResponse.success("User registered Successfully", new Object()));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request,
                                        HttpServletResponse response) {
        LoginResponseDto responseDto = authService.loginUser(loginRequestDto, request, response);
        return ResponseEntity.ok(ApiResponse.success("Login Success", responseDto));
    }

    @PostMapping(path = "/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser(request, response);
        return ResponseEntity.ok(ApiResponse.success("User Logged Out successfully", new Object()));
    }
}

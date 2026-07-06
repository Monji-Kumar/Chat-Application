package com.monji.chatapp.auth_service.controller;

import com.monji.chatapp.auth_service.dto.LoginRequestDto;
import com.monji.chatapp.auth_service.dto.RegisterRequestDto;
import com.monji.chatapp.auth_service.service.AuthService;
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
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto registerRequestDto)
    {
        authService.registerUser(registerRequestDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request,
                                        HttpServletResponse response) {
        authService.loginUser(loginRequestDto, request, response);
        return ResponseEntity.ok("User Logged in successfully");
    }

    @PostMapping(path = "/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser(request, response);
        return ResponseEntity.ok("User Logged Out successfully");
    }
}

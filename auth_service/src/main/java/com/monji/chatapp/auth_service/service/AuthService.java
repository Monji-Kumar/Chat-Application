package com.monji.chatapp.auth_service.service;

import com.monji.chatapp.auth_service.dto.LoginRequestDto;
import com.monji.chatapp.auth_service.dto.RegisterRequestDto;
import com.monji.chatapp.auth_service.entity.User;
import com.monji.chatapp.auth_service.exception.InvalidCredentialsException;
import com.monji.chatapp.auth_service.exception.UserAlreadyExistsException;
import com.monji.chatapp.auth_service.repository.UserRepository;
import com.monji.chatapp.common.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    public void registerUser(RegisterRequestDto registerRequestDto) {
        String username = registerRequestDto.getUsername();
        String password = registerRequestDto.getPassword();
        String email = registerRequestDto.getEmail();
        String phone = registerRequestDto.getPhone();
        String name = registerRequestDto.getName();

        Optional<User> userOpt = userRepository.findByEmailOrPhoneOrUsername(email, phone, username);
       if(userOpt.isPresent()){
           throw new UserAlreadyExistsException("User already exists");
       }

       String hashedPassword = passwordEncoder.encode(password);

       User user = User.builder()
               .username(username)
               .password(hashedPassword)
               .email(email)
               .phone(phone)
               .name(name)
               .build();

       userRepository.save(user);
    }

    public void loginUser(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        String  username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(!userOpt.isPresent()){
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userOpt.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid username or password");
        }

        setAuthCookies(user, request, response);
    }

    public void refreshToken(String refreshToken) {

    }

    private void setAuthCookies(User user, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername(), user.getRole().name());
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}

package com.orderingsystem.authservice.controllers;

import com.orderingsystem.authservice.dtos.LoginUserDto;
import com.orderingsystem.authservice.dtos.RegisterUserDto;
import com.orderingsystem.authservice.entities.RegisterNotification;
import com.orderingsystem.authservice.entities.User;
import com.orderingsystem.authservice.response.LoginResponse;
import com.orderingsystem.authservice.services.AuthenticationService;
import com.orderingsystem.authservice.services.JwtService;
import com.orderingsystem.authservice.services.RabbitMQSender;
import com.orderingsystem.authservice.services.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private  final TokenBlacklistService tokenBlacklistService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        RegisterNotification registerNotification = new RegisterNotification();

        registerNotification.setEmail(registerUserDto.getEmail());
        registerNotification.setFullName(registerUserDto.getFullName());
        registerNotification.setType("Register Notification");
        rabbitMQSender.sendRegisterNotification(registerNotification);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // Get remaining expiry from token
        long expirationInMillis = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

        // Blacklist token
        tokenBlacklistService.blacklistToken(token, Duration.ofMillis(expirationInMillis));

        return ResponseEntity.ok("Logged out successfully");
    }


}
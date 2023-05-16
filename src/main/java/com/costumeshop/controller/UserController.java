package com.costumeshop.controller;

import com.costumeshop.model.request.LoginRequest;
import com.costumeshop.model.request.RegistrationRequest;
import com.costumeshop.core.security.user.UserDetailsImpl;
import com.costumeshop.core.security.jwt.JwtUtils;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.model.response.UserLoginResponse;
import com.costumeshop.model.response.RegistrationResponse;
import com.costumeshop.model.response.UserResponse;
import com.costumeshop.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final DatabaseService databaseService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @PostMapping(path = "/registration")
    public @ResponseBody ResponseEntity<?> registerNewUser(@RequestBody RegistrationRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        User existingUserByEmail = databaseService.findUserByEmail(request.getEmail());
        if (existingUserByEmail != null) {
            return new ResponseEntity<>(RegistrationResponse.builder()
                    .success(false)
                    .message("User with that email address already exists!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        User existingUserByUsername = databaseService.findUserByUsername(request.getEmail());
        if (existingUserByUsername != null) {
            return new ResponseEntity<>(RegistrationResponse.builder()
                    .success(false)
                    .message("User with that username address already exists!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        try {
            databaseService.insertNewRegisteredUser(request);
        } catch (Exception e) {
            return new ResponseEntity<>(RegistrationResponse.builder()
                    .success(false)
                    .message("Failed to insert new user data into the database!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(RegistrationResponse.builder()
                .success(true)
                .message("Registration successful!")
                .build(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> login(@RequestBody LoginRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        User user = databaseService.findUserByUsernameOrEmail(request.getEmail());
        Integer emailVerified = user.getEmailVerified();
        if (emailVerified == null || emailVerified.equals(0)) {
            return new ResponseEntity<>(UserLoginResponse.builder()
                    .success(false)
                    .message("User account is not verified! Click on the verification link that has been mailed to you.")
                    .build(), responseHeaders, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(UserLoginResponse.builder()
                    .success(false)
                    .message("Authentication error! Is your email/username/password valid?")
                    .build(), responseHeaders, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new ResponseEntity<>(UserLoginResponse.builder()
                .token(token)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .success(true)
                .message("Login successful! Hello, " + userDetails.getUsername() + "!")
                .build(), responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/users")
    public @ResponseBody ResponseEntity<?> getUserByVerificationToken(@RequestParam String verificationToken) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        User user = databaseService.findUserByVerificationToken(verificationToken);
        if (user != null) {
            return new ResponseEntity<>(UserResponse.builder()
                    .user(user)
                    .success(true)
                    .message("User found!")
                    .build(), responseHeaders, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(UserResponse.builder()
                    .success(false)
                    .message("User not found!")
                    .build(), responseHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/verification")
    public @ResponseBody ResponseEntity<?> verifyUser(@RequestParam Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.verifyUser(userId);
            return new ResponseEntity<>(UserResponse.builder()
                    .success(true)
                    .message("User verified!")
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(UserResponse.builder()
                    .success(false)
                    .message("Failed to verify user: " + e.getMessage())
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }
}


package com.costumeshop.controller;

import com.costumeshop.controller.criteria.LoginCriteria;
import com.costumeshop.controller.criteria.RegistrationCriteria;
import com.costumeshop.core.security.user.UserDetailsImpl;
import com.costumeshop.core.security.jwt.JwtUtils;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.model.response.JwtResponse;
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
    public @ResponseBody ResponseEntity<?> registerNewUser(@RequestBody RegistrationCriteria criteria) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        User existingUser = databaseService.findUserByEmail(criteria.getEmail());
        if (existingUser != null) {
            return new ResponseEntity<>(RegistrationResponse.builder()
                    .success(false)
                    .message("User with that email address already exists!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        try {
            databaseService.insertNewRegisteredUser(criteria);
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
    public @ResponseBody ResponseEntity<?> login(@RequestBody LoginCriteria criteria) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(criteria.getEmail(), criteria.getPassword()));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(JwtResponse.builder()
                    .success(false)
                    .message("Authentication error! Is your email/username/password valid?")
                    .build(), responseHeaders, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new ResponseEntity<>(JwtResponse.builder()
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


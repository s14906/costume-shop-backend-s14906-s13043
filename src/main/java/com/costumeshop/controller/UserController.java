package com.costumeshop.controller;

import com.costumeshop.core.security.jwt.JwtUtils;
import com.costumeshop.core.security.user.UserDetailsImpl;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.entity.UserRole;
import com.costumeshop.model.dto.AddressDTO;
import com.costumeshop.model.dto.UserDTO;
import com.costumeshop.model.dto.UserLoginDTO;
import com.costumeshop.model.dto.UserRegistrationDTO;
import com.costumeshop.model.response.GetAddressesResponse;
import com.costumeshop.model.response.SimpleResponse;
import com.costumeshop.model.response.UserResponse;
import com.costumeshop.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    //TODO: logging
    private final DatabaseService databaseService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @PostMapping(path = "/registration")
    public @ResponseBody ResponseEntity<?> registerNewUser(@RequestBody UserRegistrationDTO dto) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        User existingUserByEmail = databaseService.findUserByEmail(dto.getEmail());
        if (existingUserByEmail != null) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("User with that email address already exists!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        User existingUserByUsername = databaseService.findUserByUsername(dto.getEmail());
        if (existingUserByUsername != null) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("User with that username address already exists!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        try {
            databaseService.insertNewRegisteredUser(dto);
        } catch (Exception e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Failed to insert new user data into the database!")
                    .build(), responseHeaders, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(SimpleResponse.builder()
                .success(true)
                .message("Registration successful!")
                .build(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        User user = databaseService.findUserByUsernameOrEmail(dto.getEmail());
        if (user == null) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("User with that username or email not found in the database!")
                    .build(), responseHeaders, HttpStatus.NOT_FOUND);
        }

        Integer emailVerified = user.getEmailVerified();
        if (emailVerified == null || emailVerified.equals(0)) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("User account is not verified! Click on the verification link that has been mailed to you.")
                    .build(), responseHeaders, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(SimpleResponse.builder()
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

        UserDTO userDTO = UserDTO.builder()
                .token(token)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .phone(user.getPhone())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();

        return new ResponseEntity<>(UserResponse.builder()
                .user(userDTO)
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
            UserDTO userDTO = UserDTO.builder()
                    .id(Long.valueOf(user.getId()))
                    .password(user.getPassword())
                    .token(user.getVerificationToken())
                    .emailVerified(user.getEmailVerified())
                    .email(user.getEmail())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .roles(user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList()))
                    .build();
            return new ResponseEntity<>(UserResponse.builder()
                    .user(userDTO)
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
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message("User verified!")
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Failed to verify user: " + e.getMessage())
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @PostMapping("/add-address")
    public @ResponseBody ResponseEntity<?> addAddress(@RequestBody AddressDTO request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.insertNewAddressForUser(request);
        } catch (Exception e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Error occurred when adding address!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SimpleResponse.builder()
                .success(true)
                .message("Address added!")
                .build(), responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/get-addresses")
    public @ResponseBody ResponseEntity<?> getAddressesForUser(@RequestParam Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        List<AddressDTO> addresses;
        try {
            addresses = databaseService.getAddressesForUser(userId);
        } catch (Exception e) {
            return new ResponseEntity<>(GetAddressesResponse.builder()
                    .success(false)
                    .message("Error occurred when retrieving addresses!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(GetAddressesResponse.builder()
                .success(true)
                .message("Addresses retrieved!")
                .addresses(addresses)
                .build(), responseHeaders, HttpStatus.OK);

    }

    @PostMapping("/remove-address")
    public @ResponseBody ResponseEntity<?> removeAddress(@RequestParam Integer addressId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.deleteAddress(addressId);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Error occurred when removing address! The address is connected to an existing complaint!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Error occurred when removing address!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SimpleResponse.builder()
                .success(true)
                .message("Address removed!")
                .build(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public @ResponseBody ResponseEntity<?> changePassword(@RequestParam Integer userId,
                                                          @RequestParam String newPassword) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.changePasswordForUser(userId, newPassword);
        } catch (Exception e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Error occurred when changing password!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SimpleResponse.builder()
                .success(true)
                .message("Password changed!")
                .build(), responseHeaders, HttpStatus.OK);
    }

}


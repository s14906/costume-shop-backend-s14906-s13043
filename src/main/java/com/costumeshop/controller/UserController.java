package com.costumeshop.controller;

import com.costumeshop.controller.criteria.LoginCriteria;
import com.costumeshop.controller.criteria.RegistrationCriteria;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.UserRepository;
import com.costumeshop.service.JsonCreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JsonCreatorService jsonCreatorService;

    @PostMapping(path = "/registration")
    public @ResponseBody ResponseEntity<String> registerNewUser(@RequestBody RegistrationCriteria criteria) {
        User user = userRepository.findByEmail(criteria.getEmail());
        String responseBody;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (user != null) {
            responseBody = jsonCreatorService.createJsonResponse(false, "User with that email address already exists!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.FORBIDDEN);
        }

        User newUser = new User();
        newUser.setName(criteria.getName());
        newUser.setEmail(criteria.getEmail());
        newUser.setSurname(criteria.getSurname());
        newUser.setPassword(passwordEncoder.encode(criteria.getPassword()));
        newUser.setUserRoleId(1);
        userRepository.save(newUser);

        responseBody = jsonCreatorService.createJsonResponse(true, "User registration success!");
        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> login(@RequestBody LoginCriteria criteria) {
        User user = userRepository.findByEmail(criteria.getEmail());
        boolean authenticated = user != null && passwordEncoder.matches(criteria.getPassword(), user.getPassword());
        String responseBody;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (authenticated) {
            responseBody = jsonCreatorService.createJsonResponse(true, "Logged in!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
        } else {
            responseBody = jsonCreatorService.createJsonResponse(false, "Invalid username or password!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.UNAUTHORIZED);
        }
    }
}


package com.costumeshop.controller;

import com.costumeshop.controller.criteria.LoginCriteria;
import com.costumeshop.controller.criteria.RegistrationCriteria;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.service.DatabaseService;
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
@CrossOrigin
public class UserController {
    private final DatabaseService databaseService;
    private final PasswordEncoder passwordEncoder;
    private final JsonCreatorService jsonCreatorService;

    @PostMapping(path = "/registration")
    public @ResponseBody ResponseEntity<String> registerNewUser(@RequestBody RegistrationCriteria criteria) {
        String responseBody;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        User existingUser = databaseService.findUserByEmail(criteria.getEmail());
        if (existingUser != null) {
            responseBody = jsonCreatorService.createJsonResponse(false, "User with that email address already exists!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.FORBIDDEN);
        }

        databaseService.insertNewRegisteredUser(criteria);

        responseBody = jsonCreatorService.createJsonResponse(true, "User registration success!");
        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> login(@RequestBody LoginCriteria criteria) {
        String responseBody;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        User user = databaseService.findUserByEmail(criteria.getEmail());
        boolean authenticated = user != null && passwordEncoder.matches(criteria.getPassword(), user.getPassword());

        if (authenticated) {
            responseBody = jsonCreatorService.createJsonResponse(true, "Logged in!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
        } else {
            responseBody = jsonCreatorService.createJsonResponse(false, "Invalid username or password!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.UNAUTHORIZED);
        }
    }
}


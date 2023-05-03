package com.costumeshop.core.security.controller;

import com.costumeshop.core.security.controller.criteria.LoginCriteria;
import com.costumeshop.core.security.controller.criteria.RegistrationCriteria;
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

@CrossOrigin
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JsonCreatorService jsonCreatorService;

    @CrossOrigin
    @PostMapping(path = "/registration")
    public @ResponseBody ResponseEntity<String> registerNewUser(@RequestBody RegistrationCriteria criteria) {
        User user = new User();
        user.setName(criteria.getName());
        user.setEmail(criteria.getEmail());
        user.setSurname(criteria.getSurname());
        user.setPassword(passwordEncoder.encode(criteria.getPassword()));
        user.setUserRoleId(1);
        userRepository.save(user);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        String responseBody = jsonCreatorService.createJsonResponse(true, "User registration success!");
        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> login(@RequestBody LoginCriteria criteria) {
        User user = userRepository.findByEmail(criteria.getEmail());
        boolean authenticated = user != null && passwordEncoder.matches(criteria.getPassword(), user.getPassword());
        String responseBody;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (authenticated) {
            responseBody = jsonCreatorService.createJsonResponse(true, "Success!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
        } else {
            responseBody = jsonCreatorService.createJsonResponse(false, "Invalid username or password!");
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.UNAUTHORIZED);
        }

    }
}


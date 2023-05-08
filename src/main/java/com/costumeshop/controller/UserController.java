package com.costumeshop.controller;

import com.costumeshop.controller.criteria.LoginCriteria;
import com.costumeshop.controller.criteria.RegistrationCriteria;
import com.costumeshop.controller.payload.JwtResponse;
import com.costumeshop.core.security.UserDetailsImpl;
import com.costumeshop.core.security.jwt.JwtUtils;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.UserRoleRepository;
import com.costumeshop.service.DatabaseService;
import com.costumeshop.service.JsonCreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final DatabaseService databaseService;
    private final PasswordEncoder passwordEncoder;
    private final JsonCreatorService jsonCreatorService;
    private final AuthenticationManager authenticationManager;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtils jwtUtils;


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
    public @ResponseBody ResponseEntity<?> login(@RequestBody LoginCriteria criteria) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(criteria.getEmail(), criteria.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}


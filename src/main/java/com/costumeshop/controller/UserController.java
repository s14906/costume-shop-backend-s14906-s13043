package com.costumeshop.controller;

import com.costumeshop.core.security.jwt.JwtUtils;
import com.costumeshop.core.security.user.UserDetailsImpl;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.exception.DataException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.AddressDTO;
import com.costumeshop.model.dto.UserDTO;
import com.costumeshop.model.dto.UserLoginDTO;
import com.costumeshop.model.dto.UserRegistrationDTO;
import com.costumeshop.model.response.GetAddressesResponse;
import com.costumeshop.model.response.SimpleResponse;
import com.costumeshop.model.response.UserResponse;
import com.costumeshop.service.DataMapperService;
import com.costumeshop.service.database.AddressDatabaseService;
import com.costumeshop.service.database.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserDatabaseService userDatabaseService;
    private final AddressDatabaseService addressDatabaseService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final DataMapperService dataMapperService;


    @PostMapping(path = "/registration")
    public @ResponseBody ResponseEntity<?> registerNewUser(@RequestBody UserRegistrationDTO dto) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            User existingUserByEmail = userDatabaseService.findUserByEmail(dto.getEmail());
            if (existingUserByEmail != null) {
                throw new DataException(ErrorCode.ERR_009);
            }

            User existingUserByUsername = userDatabaseService.findUserByUsername(dto.getEmail());
            if (existingUserByUsername != null) {
                throw new DataException(ErrorCode.ERR_010);
            }

            Integer userId = userDatabaseService.insertNewUserByUserRegistrationDTO(dto);

            CodeMessageUtils.logMessage(InfoCode.INFO_017, userId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_003))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_008, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_008, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            User user = userDatabaseService.findUserByUsernameOrEmail(dto.getEmail());
            Integer emailVerified = user.getEmailVerified();
            if (emailVerified == null || emailVerified.equals(0)) {
                throw new DataException(ErrorCode.ERR_012);
            }

            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
            } catch (AuthenticationException e) {
                e.printStackTrace();
                throw new DataException(ErrorCode.ERR_013);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            UserDTO userDTO = dataMapperService.userWithUserDetailsToUserDTO(user, token, userDetails, roles);
            CodeMessageUtils.logMessage(InfoCode.INFO_012, userDetails.getId(), logger);
            return new ResponseEntity<>(UserResponse.builder()
                    .user(userDTO)
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_004, userDetails.getUsername()))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_031, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_031, e))
                    .build(), responseHeaders, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users")
    public @ResponseBody ResponseEntity<?> getUserByVerificationToken(@RequestParam String verificationToken) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            User user = userDatabaseService.findUserByVerificationToken(verificationToken);

            UserDTO userDTO = dataMapperService.userToUserDTO(user);
            CodeMessageUtils.logMessage(InfoCode.INFO_019, userDTO.getId(), logger);
            return new ResponseEntity<>(UserResponse.builder()
                    .user(userDTO)
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_005))
                    .build(), responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_035, e, logger);
            return new ResponseEntity<>(UserResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_035, e))
                    .build(), responseHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/verification")
    public @ResponseBody ResponseEntity<?> verifyUser(@RequestParam Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            userDatabaseService.verifyUser(userId);
            CodeMessageUtils.logMessage(InfoCode.INFO_020, userId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_007))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_029, userId, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_029, userId, e))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @PostMapping("/add-address")
    public @ResponseBody ResponseEntity<?> addAddress(@RequestBody AddressDTO request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            Integer addressId = addressDatabaseService.insertNewAddressByAddressDTO(request);
            CodeMessageUtils.logMessage(InfoCode.INFO_014, addressId, request.getUserId(), logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_013))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_036, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_036, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-addresses")
    public @ResponseBody ResponseEntity<?> getAddressesForUser(@RequestParam Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<AddressDTO> addresses = addressDatabaseService.findAddressesByUserId(userId);
            CodeMessageUtils.logMessage(InfoCode.INFO_015, userId, logger);
            return new ResponseEntity<>(GetAddressesResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_008))
                    .addresses(addresses)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_037, e, logger);
            return new ResponseEntity<>(GetAddressesResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_037, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/remove-address")
    public @ResponseBody ResponseEntity<?> removeAddress(@RequestParam Integer addressId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            addressDatabaseService.deleteAddressById(addressId);
            CodeMessageUtils.logMessage(InfoCode.INFO_016, addressId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_009))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_039, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_039, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_040, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_040, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/change-password")
    public @ResponseBody ResponseEntity<?> changePassword(@RequestParam Integer userId,
                                                          @RequestParam String newPassword) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            userDatabaseService.changePasswordForUser(userId, newPassword);
            CodeMessageUtils.logMessage(InfoCode.INFO_018, userId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_010))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_041, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_041, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


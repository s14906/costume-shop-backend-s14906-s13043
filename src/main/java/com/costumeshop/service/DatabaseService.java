package com.costumeshop.service;

import com.costumeshop.controller.criteria.RegistrationCriteria;
import com.costumeshop.core.sql.entity.Address;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.entity.UserRole;
import com.costumeshop.core.sql.repository.AddressRepository;
import com.costumeshop.core.sql.repository.UserRepository;
import com.costumeshop.core.sql.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AddressRepository addressRepository;
    private final MailService mailService;

    @Transactional
    public void insertNewRegisteredUser(RegistrationCriteria criteria) {
        User newUser = new User();
        newUser.setName(criteria.getName());
        newUser.setEmail(criteria.getEmail());
        newUser.setUsername(criteria.getUsername());
        newUser.setSurname(criteria.getSurname());
        newUser.setPassword(passwordEncoder.encode(criteria.getPassword()));
        newUser.setPhone(criteria.getPhone());
        UserRole userRole = userRoleRepository.findById(1).orElseThrow();
        newUser.setUserRoles(Set.of(userRole));

        String verificationToken = UUID.randomUUID().toString();
        newUser.setVerificationToken(verificationToken);

        userRepository.save(newUser);

        try {
            mailService.sendVerificationEmail(newUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        Address address = new Address();
        address.setCity(criteria.getCity());
        address.setStreet(criteria.getStreet());
        address.setPostalCode(criteria.getPostalCode());
        address.setFlatNumber(criteria.getFlatNumber());
        address.setUser(newUser);
        addressRepository.save(address);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken);
    }

    public void verifyUser(Integer userId) {
        userRepository.save(userRepository.findById(userId).orElseThrow());
    }
}

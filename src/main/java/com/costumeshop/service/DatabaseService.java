package com.costumeshop.service;

import com.costumeshop.controller.criteria.RegistrationCriteria;
import com.costumeshop.core.sql.entity.Address;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.AddressRepository;
import com.costumeshop.core.sql.repository.UserRepository;
import com.costumeshop.core.sql.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public void insertNewRegisteredUser(RegistrationCriteria criteria) {
        User newUser = new User();
        newUser.setName(criteria.getName());
        newUser.setEmail(criteria.getEmail());
        newUser.setSurname(criteria.getSurname());
        newUser.setPassword(passwordEncoder.encode(criteria.getPassword()));
        newUser.setUserRole(userRoleRepository.findById(1).get());
        newUser.setPhone(criteria.getPhone());
        userRepository.save(newUser);

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
}

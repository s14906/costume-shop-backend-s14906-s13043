package com.costumeshop.service.database;

import com.costumeshop.core.sql.entity.Address;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.entity.UserRole;
import com.costumeshop.core.sql.repository.UserRepository;
import com.costumeshop.core.sql.repository.UserRoleRepository;
import com.costumeshop.exception.DataException;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.UserRegistrationDTO;
import com.costumeshop.service.DataMapperService;
import com.costumeshop.service.MailService;
import com.costumeshop.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseService.class);

    private final DataMapperService dataMapperService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AddressDatabaseService addressDatabaseService;
    private final PasswordService passwordService;
    private final MailService mailService;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByVerificationToken(String verificationToken) {
        return Optional.ofNullable(userRepository.findByVerificationToken(verificationToken))
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_019));
    }

    public User findUserByUsernameOrEmail(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            CodeMessageUtils.logMessage(InfoCode.INFO_001, username, logger);
            user = userRepository.findByEmail(username);
        }
        if (user == null) {
            CodeMessageUtils.logMessage(InfoCode.INFO_002, username, logger);
            throw new DataException(ErrorCode.ERR_032);
        }
        return user;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_027, id));
    }

    public Integer insertNewUserByUserRegistrationDTO(UserRegistrationDTO userRegistrationDTO) {
        try {
            UserRole userRole = userRoleRepository.findById(1).orElseThrow();
            User newUser = dataMapperService.userRegistrationDTOtoUser(userRegistrationDTO, userRole);

            String verificationToken = UUID.randomUUID().toString();
            newUser.setVerificationToken(verificationToken);

            userRepository.save(newUser);

            Address address = dataMapperService.addressDTOToUser(userRegistrationDTO, newUser);
            addressDatabaseService.insertNewAddress(address);

            mailService.sendVerificationEmail(newUser);
            return newUser.getId();

        } catch (Exception e) {
            throw new DatabaseException(ErrorCode.ERR_034, e);
        }
    }


    public void verifyUser(Integer userId) {
        try {
            if (userId == null) {
                throw new DataException(ErrorCode.ERR_017);
            }
            User user = userRepository
                    .findById(userId)
                    .orElseThrow(() -> new DataException(ErrorCode.ERR_027, userId));
            user.setEmailVerified(1);
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException(ErrorCode.ERR_028, e.getMessage());
        }
    }

    public void changePasswordForUser(Integer userId, String newPassword) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, userId));
            String encodedPassword;
            try {
                encodedPassword = passwordService.encodePassword(newPassword);
            } catch (Exception e) {
                throw new DataException(ErrorCode.ERR_025, e);
            }

            if (encodedPassword != null) {
                user.setPassword(encodedPassword);
                userRepository.save(user);
            } else {
                throw new DataException(ErrorCode.ERR_025);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }
}

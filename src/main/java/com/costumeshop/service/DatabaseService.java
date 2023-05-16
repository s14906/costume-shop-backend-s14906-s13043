package com.costumeshop.service;

import com.costumeshop.core.sql.entity.*;
import com.costumeshop.core.sql.repository.*;
import com.costumeshop.model.request.AddToCartRequest;
import com.costumeshop.model.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final ItemRepository itemRepository;
    private final ItemSizeRepository itemSizeRepository;
    private final ItemCartRepository itemCartRepository;
    private final ItemColorRepository itemColorRepository;
    private final MailService mailService;

    @Transactional
    public void insertNewRegisteredUser(RegistrationRequest request) {
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setSurname(request.getSurname());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhone(request.getPhone());
        newUser.setEmailVerified(0);
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
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setPostalCode(request.getPostalCode());
        address.setFlatNumber(request.getFlatNumber());
        address.setUser(newUser);
        addressRepository.save(address);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken);
    }

    public User findUserByUsernameOrEmail(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        return user;
    }

    public void verifyUser(Integer userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow();
        user.setEmailVerified(1);
        userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow();
    }
    public Item findItemById(Integer id) {
        return itemRepository.findById(id).orElseThrow();
    }
    public List<Item> findAllItems() {
        return IterableUtils.toList(itemRepository.findAll());
    }

    public ItemSize findItemSizeById(Integer id) {
        return itemSizeRepository.findById(id).orElseThrow();
    }
    public List<ItemSize> findAllItemSizes() {
        return IterableUtils.toList(itemSizeRepository.findAll());
    }

    public List<ItemColor> findAllItemColors() {
        return IterableUtils.toList(itemColorRepository.findAll());
    }

    public void insertItemToCart(AddToCartRequest request) {
        User user = findUserById(request.getUserId());
        Item item = findItemById(request.getItemId());
        ItemSize itemSize = findItemSizeById(request.getItemSizeId());

        List<ItemCart> existingCartItems =
                itemCartRepository.findAllByUserAndItemAndItemSize(user, item, itemSize);

        if (!existingCartItems.isEmpty()) {
            existingCartItems.forEach(itemCart ->
                    itemCart.setItemCount(itemCart.getItemCount() + 1));
        }

        ItemCart itemCart = new ItemCart();
        itemCart.setUser(user);
        itemCart.setItem(item);
        itemCart.setItemSize(itemSize);
        itemCartRepository.save(itemCart);
    }

    public List<ItemCart> findCartItemsForUser(Integer userId) {
        return IterableUtils.toList(itemCartRepository.findAllByUserId(userId));
    }
}

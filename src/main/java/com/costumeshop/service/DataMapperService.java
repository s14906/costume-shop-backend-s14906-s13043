package com.costumeshop.service;

import com.costumeshop.core.security.user.UserDetailsImpl;
import com.costumeshop.core.sql.entity.*;
import com.costumeshop.exception.DataException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataMapperService {
    private static final Integer MAX_FIELD_LENGTH = 30;
    private final PasswordEncoder passwordEncoder;

    public User userRegistrationDTOtoUser(UserRegistrationDTO userRegistrationDTO, UserRole userRole) {
        if (userRegistrationDTO == null) {
            throw new DataException(ErrorCode.ERR_020);
        }
        if (isNullOrTooLong(userRegistrationDTO.getName())) {
            throw new DataException(ErrorCode.ERR_001);
        }

        if (isNullOrTooLong(userRegistrationDTO.getEmail())) {
            throw new DataException(ErrorCode.ERR_002);
        }

        if (isNullOrTooLong(userRegistrationDTO.getUsername())) {
            throw new DataException(ErrorCode.ERR_003);
        }

        if (isNullOrTooLong(userRegistrationDTO.getSurname())) {
            throw new DataException(ErrorCode.ERR_004);
        }

        if (isNullOrTooLong(userRegistrationDTO.getPassword())) {
            throw new DataException(ErrorCode.ERR_005);
        }

        if (isNullOrTooLong(userRegistrationDTO.getPhone())) {
            throw new DataException(ErrorCode.ERR_006);
        }

        if (isNullOrTooLong(userRole.getRole())) {
            throw new DataException(ErrorCode.ERR_007);
        }

        User newUser = new User();
        newUser.setName(userRegistrationDTO.getName());
        newUser.setEmail(userRegistrationDTO.getEmail());
        newUser.setUsername(userRegistrationDTO.getUsername());
        newUser.setSurname(userRegistrationDTO.getSurname());
        try {
            newUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        } catch (Exception e) {
            throw new DataException(ErrorCode.ERR_025, e);
        }
        newUser.setPhone(userRegistrationDTO.getPhone());
        newUser.setEmailVerified(0);
        newUser.setUserRoles(Set.of(userRole));
        return newUser;
    }

    public UserDTO userWithUserDetailsToUserDTO(User user, String token, UserDetailsImpl userDetails, List<String> roles) {
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        if (userDetails == null) {
            throw new DataException(ErrorCode.ERR_015);
        }

        validateBasicUserData(user);

        if (token == null) {
            throw new DataException(ErrorCode.ERR_016);
        }
        if (roles.isEmpty()) {
            throw new DataException(ErrorCode.ERR_018);
        }

        return UserDTO.builder()
                .token(token)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .phone(user.getPhone())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    public Address addressDTOToUser(UserRegistrationDTO userRegistrationDTO, User user) {
        if (userRegistrationDTO == null) {
            throw new DataException(ErrorCode.ERR_020);
        }
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }

        Address address = new Address();
        AddressDTO addressDTO = userRegistrationDTO.getAddress();
        if (addressDTO == null) {
            throw new DataException(ErrorCode.ERR_021);
        }
        if (isNullOrTooLong(addressDTO.getCity())) {
            throw new DataException(ErrorCode.ERR_022);
        }
        if (isNullOrTooLong(addressDTO.getPostalCode())) {
            throw new DataException(ErrorCode.ERR_023);
        }
        if (isNullOrTooLong(addressDTO.getFlatNumber())) {
            throw new DataException(ErrorCode.ERR_024);
        }
        if (isNullOrTooLong(addressDTO.getStreet())) {
            throw new DataException(ErrorCode.ERR_030);
        }

        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setFlatNumber(addressDTO.getFlatNumber());
        address.setUser(user);
        return address;
    }

    public UserDTO userToUserDTO(User user) {
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        if (user.getId() == null) {
            throw new DataException(ErrorCode.ERR_017);
        }
        if (user.getEmailVerified() == null) {
            throw new DataException(ErrorCode.ERR_026);
        }

        validateBasicUserData(user);

        if (user.getUserRoles().isEmpty()) {
            throw new DataException(ErrorCode.ERR_018);
        }

        return UserDTO.builder()
                .id(user.getId())
                .token(user.getVerificationToken())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .roles(user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList()))
                .build();
    }

    private boolean isNullOrTooLong(String value) {
        return value == null ||
                value.isEmpty() ||
                value.isBlank() ||
                value.length() > MAX_FIELD_LENGTH;
    }

    public Address addressDTOToAddress(AddressDTO addressDTO, User user) {
        if (addressDTO == null) {
            throw new DataException(ErrorCode.ERR_021);
        }
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        if (isNullOrTooLong(addressDTO.getCity())) {
            throw new DataException(ErrorCode.ERR_022);
        }
        if (isNullOrTooLong(addressDTO.getStreet())) {
            throw new DataException(ErrorCode.ERR_030);
        }
        if (isNullOrTooLong(addressDTO.getFlatNumber())) {
            throw new DataException(ErrorCode.ERR_024);
        }
        if (isNullOrTooLong(addressDTO.getPostalCode())) {
            throw new DataException(ErrorCode.ERR_023);
        }
        Address address = new Address();
        address.setUser(user);
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setFlatNumber(addressDTO.getFlatNumber());
        address.setPostalCode(addressDTO.getPostalCode());
        return address;
    }

    public AddressDTO addressToAddressDTO(Address address, Integer userId) {
        if (address == null) {
            throw new DataException(ErrorCode.ERR_021);
        }
        if (userId == null) {
            throw new DataException(ErrorCode.ERR_017);
        }
        if (isNullOrTooLong(address.getCity())) {
            throw new DataException(ErrorCode.ERR_022);
        }
        if (isNullOrTooLong(address.getStreet())) {
            throw new DataException(ErrorCode.ERR_030);
        }
        if (isNullOrTooLong(address.getFlatNumber())) {
            throw new DataException(ErrorCode.ERR_024);
        }
        if (isNullOrTooLong(address.getPostalCode())) {
            throw new DataException(ErrorCode.ERR_023);
        }

        return AddressDTO.builder()
                .addressId(address.getId())
                .userId(userId)
                .city(address.getCity())
                .flatNumber(address.getFlatNumber())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .notes(address.getNotes())
                .build();
    }

    public ItemImageDTO itemImageToItemImageDTO(ItemImage itemImage) {
        if (itemImage == null) {
            throw new DataException(ErrorCode.ERR_042);
        }
        if (itemImage.getId() == null) {
            throw new DataException(ErrorCode.ERR_043);
        }
        if (itemImage.getImageBase64() == null) {
            throw new DataException(ErrorCode.ERR_044);
        }
        return ItemImageDTO.builder()
                .imageId(itemImage.getId())
                .imageBase64(itemImage.getImageBase64())
                .build();
    }

    public ItemDTO itemToItemDTO(Item item, List<ItemImageDTO> itemImageDTOs) {
        if (itemImageDTOs.isEmpty()) {
            throw new DataException(ErrorCode.ERR_045);
        }
        if (item == null) {
            throw new DataException(ErrorCode.ERR_046);
        }
        if (item.getId() == null) {
            throw new DataException(ErrorCode.ERR_047);
        }
        if (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().isBlank()) {
            throw new DataException(ErrorCode.ERR_048);
        }
        if (item.getPrice() == null) {
            throw new DataException(ErrorCode.ERR_049);
        }
        if (item.getTitle() == null || item.getTitle().isEmpty() || item.getTitle().isBlank()) {
            throw new DataException(ErrorCode.ERR_050);
        }

        return ItemDTO.builder()
                .itemId(item.getId())
                .itemImages(itemImageDTOs)
                .description(item.getDescription())
                .price(item.getPrice())
                .title(item.getTitle())
                .quantity(item.getQuantity())
                .visible(item.getVisible())
                .itemCategory(item.getItemCategory().getCategory())
                .itemSet(item.getItemSet().getSet())
                .build();
    }

    public Item itemDTOToItem(ItemDTO itemDTO, Item item, ItemCategory itemCategory, ItemSet itemSet) {
        if (itemDTO == null || item == null) {
            throw new DataException(ErrorCode.ERR_046);
        }
        if (itemCategory == null) {
            throw new DataException(ErrorCode.ERR_114);
        }
        if (itemSet == null) {
            throw new DataException(ErrorCode.ERR_115);
        }
        if (itemDTO.getItemId() == null) {
            throw new DataException(ErrorCode.ERR_047);
        }
        String description = itemDTO.getDescription();
        if (description == null || description.isEmpty() || description.isBlank()) {
            throw new DataException(ErrorCode.ERR_048);
        }
        if (itemDTO.getPrice() == null) {
            throw new DataException(ErrorCode.ERR_049);
        }
        String title = itemDTO.getTitle();
        if (title == null || title.isEmpty() || title.isBlank()) {
            throw new DataException(ErrorCode.ERR_050);
        }
        if (itemDTO.getQuantity() == null) {
            throw new DataException(ErrorCode.ERR_107);
        }
        List<ItemImageDTO> itemImages = itemDTO.getItemImages();
        if (itemImages == null || itemImages.isEmpty()) {
            throw new DataException(ErrorCode.ERR_042);
        }

        item.setTitle(itemDTO.getTitle());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setQuantity(itemDTO.getQuantity());
        item.setVisible(itemDTO.getVisible());
        item.setItemCategory(itemCategory);
        item.setItemSet(itemSet);
        return item;
    }

    public CartItemDTO cartItemToCartItemDTO(ItemCart itemCart, Integer userId) {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        Item item = itemCart.getItem();

        if (item == null) {
            throw new DataException(ErrorCode.ERR_046);
        }

        for (int i = 0; i < itemCart.getItemAmount(); i++) {
            ItemDTO itemDTO = ItemDTO.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .build();
            itemDTOs.add(itemDTO);
        }

        return CartItemDTO.builder()
                .cartItemId(itemCart.getId())
                .items(itemDTOs)
                .size(itemCart.getItemSize().getSize())
                .price(itemCart.getItem().getPrice())
                .title(itemCart.getItem().getTitle())
                .build();
    }

    public OrderDTO orderToOrderDTO(Order order, User user) {
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        validateBasicUserData(user);
        if (order == null) {
            throw new DataException(ErrorCode.ERR_059);
        }
        if (order.getId() == null) {
            throw new DataException(ErrorCode.ERR_061);
        }
        if (order.getOrderStatus() == null) {
            throw new DataException(ErrorCode.ERR_060, order.getId());
        }

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

        BigDecimal totalPrice = order.getOrdersDetails().stream()
                .map(orderDetails -> orderDetails.getItem().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderDTO.builder()
                .orderId(order.getId())
                .createdDate(order.getCreatedDate())
                .totalPrice(totalPrice)
                .orderStatus(order.getOrderStatus().getStatus())
                .user(userDTO)
                .build();
    }

    public ComplaintDTO complaintToComplaintDTO(Complaint complaint) {
        if (complaint == null) {
            throw new DataException(ErrorCode.ERR_063);
        }
        User employee = complaint.getUser();
        if (complaint.getId() == null) {
            throw new DataException(ErrorCode.ERR_064);
        }
        User buyer = complaint.getOrder().getUser();
        if (buyer.getId() == null) {
            throw new DataException(ErrorCode.ERR_065, complaint.getId());
        }
        if (buyer.getName() == null) {
            throw new DataException(ErrorCode.ERR_066, complaint.getId());
        }
        if (complaint.getComplaintStatus() == null) {
            throw new DataException(ErrorCode.ERR_067, complaint.getId());
        }
        if (complaint.getCreatedDate() == null) {
            throw new DataException(ErrorCode.ERR_068, complaint.getId());
        }

        return ComplaintDTO.builder()
                .complaintId(complaint.getId())
                .buyerId(complaint.getOrder().getUser().getId())
                .employeeId(employee != null ? employee.getId() : null)
                .buyerName(buyer.getName())
                .buyerSurname(buyer.getSurname())
                .complaintStatus(complaint.getComplaintStatus().getStatus())
                .employeeName(employee != null ? employee.getName() : null)
                .employeeSurname(employee != null ? employee.getSurname() : null)
                .createdDate(complaint.getCreatedDate())
                .build();
    }

    public ComplaintChatMessageDTO complaintChatMessageToComplaintChatMessageDTO(Integer complaintId,
                                                                                 ComplaintChatMessage complaintChatMessage,
                                                                                 Set<String> complaintChatImagesBase64,
                                                                                 UserDTO userDTO) {
        if (complaintId == null) {
            throw new DataException(ErrorCode.ERR_064);
        }
        if (complaintChatMessage.getId() == null) {
            throw new DataException(ErrorCode.ERR_072);
        }
        String chatMessage = complaintChatMessage.getChatMessage();
        if (chatMessage == null ||
                chatMessage.isEmpty() ||
                chatMessage.isBlank()) {
            throw new DataException(ErrorCode.ERR_073, complaintChatMessage.getId());
        }
        if (complaintChatMessage.getCreatedDate() == null) {
            throw new DataException(ErrorCode.ERR_074, complaintChatMessage.getId());
        }

        return ComplaintChatMessageDTO.builder()
                .complaintId(complaintId)
                .chatMessageId(complaintChatMessage.getId())
                .chatMessage(complaintChatMessage.getChatMessage())
                .createdDate(complaintChatMessage.getCreatedDate())
                .user(userDTO)
                .chatImagesBase64(complaintChatImagesBase64)
                .build();
    }

    public ComplaintChatMessage complaintChatMessageDTOToComplaintChatMessage(ComplaintChatMessageDTO complaintChatMessageDTO,
                                                                              User user,
                                                                              Complaint complaint) {

        if (complaint == null) {
            throw new DataException(ErrorCode.ERR_063);
        }
        if (complaintChatMessageDTO == null) {
            throw new DataException(ErrorCode.ERR_076);
        }
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        if (complaintChatMessageDTO.getChatMessageId() == null) {
            throw new DataException(ErrorCode.ERR_072);
        }
        String chatMessage = complaintChatMessageDTO.getChatMessage();
        if (chatMessage == null ||
                chatMessage.isEmpty() ||
                chatMessage.isBlank()) {
            throw new DataException(ErrorCode.ERR_073, complaintChatMessageDTO.getChatMessageId());
        }

        ComplaintChatMessage complaintChatMessage = new ComplaintChatMessage();
        complaintChatMessage.setComplaint(complaint);
        complaintChatMessage.setChatMessage(complaintChatMessageDTO.getChatMessage());
        complaintChatMessage.setUser(user);
        complaintChatMessage.setUser(complaintChatMessage.getUser());
        complaintChatMessage.setCreatedDate(new Date());
        return complaintChatMessage;
    }

    public OrderDetailsDTO orderToOrderDetailsDTO(Order order, Set<ItemDTO> itemDTOs,
                                                  ComplaintDTO complaintDTO) {

        if (order == null) {
            throw new DataException(ErrorCode.ERR_059);
        }
        if (order.getId() == null) {
            throw new DataException(ErrorCode.ERR_061);
        }
        if (order.getUser().getId() == null) {
            throw new DataException(ErrorCode.ERR_083, order.getId());
        }
        if (order.getCreatedDate() == null) {
            throw new DataException(ErrorCode.ERR_084, order.getId());
        }

        return OrderDetailsDTO.builder()
                .orderId(order.getId())
                .buyerId(order.getUser().getId())
                .orderDate(order.getCreatedDate())
                .complaint(complaintDTO)
                .items(itemDTOs)
                .build();
    }

    public void addItemToItemDTOList(List<ItemDTO> itemDTOs, Item item) {
        List<ItemImageDTO> itemImageDTOs = new ArrayList<>();
        for (ItemImage itemImage : item.getItemImages()) {
            ItemImageDTO itemImageDTO = itemImageToItemImageDTO(itemImage);
            itemImageDTOs.add(itemImageDTO);
        }
        ItemDTO itemDTO = itemToItemDTO(item, itemImageDTOs);
        itemDTOs.add(itemDTO);
    }

    public Order orderDTOToOrder(User user, Address address, OrderStatus orderStatus) {
        if (user == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        if (address == null) {
            throw new DataException(ErrorCode.ERR_021);
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(orderStatus);
        order.setCreatedDate(new Date());
        order.setLastModifiedDate(new Date());
        order.setAddress(address);

        return order;
    }

    public PaymentTransaction paymentTransactionDTOToPaymentTransaction(PaymentTransactionDTO paymentTransactionDTO,
                                                                        Order order,
                                                                        PaymentStatus paymentStatus) {
        if (paymentTransactionDTO == null) {
            throw new DataException(ErrorCode.ERR_098);
        }
        if (paymentTransactionDTO.getOrderId() == null) {
            throw new DataException(ErrorCode.ERR_059);
        }
        if (paymentTransactionDTO.getPaidAmount() == null) {
            throw new DataException(ErrorCode.ERR_099);
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setOrder(order);
        paymentTransaction.setPaymentStatus(paymentStatus);
        paymentTransaction.setCreatedDate(new Date());
        paymentTransaction.setLastModifiedDate(new Date());
        paymentTransaction.setHttpCodeResponse("200");
        paymentTransaction.setPaidAmount(paymentTransactionDTO.getPaidAmount());

        return paymentTransaction;
    }

    private void validateBasicUserData(User user) {
        if (user.getId() == null) {
            throw new DataException(ErrorCode.ERR_017);
        }
        if (isNullOrTooLong(user.getName())) {
            throw new DataException(ErrorCode.ERR_001);
        }
        if (isNullOrTooLong(user.getSurname())) {
            throw new DataException(ErrorCode.ERR_004);
        }
        if (isNullOrTooLong(user.getEmail())) {
            throw new DataException(ErrorCode.ERR_002);
        }
        if (isNullOrTooLong(user.getPhone())) {
            throw new DataException(ErrorCode.ERR_006);
        }
    }
}

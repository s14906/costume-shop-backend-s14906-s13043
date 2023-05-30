package com.costumeshop.service;

import com.costumeshop.model.dto.*;
import com.costumeshop.core.sql.entity.*;
import com.costumeshop.core.sql.repository.*;
import com.costumeshop.model.dto.AddressDTO;
import com.costumeshop.model.dto.AddToCartDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DatabaseService {
    //TODO: proper exception throwing/catching
    //TODO: rework the entity passing (create DTOs) to avoid lazy object loading
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AddressRepository addressRepository;
    private final ItemRepository itemRepository;
    private final ItemSizeRepository itemSizeRepository;
    private final ItemCartRepository itemCartRepository;
    private final ItemColorRepository itemColorRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintChatMessageRepository complaintChatMessageRepository;
    private final ComplaintChatImageRepository complaintChatImageRepository;
    private final ComplaintStatusRepository complaintStatusRepository;
    private final ComplaintCategoryRepository complaintCategoryRepository;
    private final OrderRepository orderRepository;
    private final MailService mailService;
    private final PasswordService passwordService;

    public void insertNewRegisteredUser(UserRegistrationDTO userRegistrationDTO) {
        User newUser = new User();
        newUser.setName(userRegistrationDTO.getName());
        newUser.setEmail(userRegistrationDTO.getEmail());
        newUser.setUsername(userRegistrationDTO.getUsername());
        newUser.setSurname(userRegistrationDTO.getSurname());
        newUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        newUser.setPhone(userRegistrationDTO.getPhone());
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
        AddressDTO addressDTO = userRegistrationDTO.getAddress();
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setFlatNumber(addressDTO.getFlatNumber());
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

    public List<ItemWithImageDTO> findAllItemsWithImages() {
        List<ItemWithImageDTO> itemWithImageDTOs = new ArrayList<>();
        for (Item item : itemRepository.findAll()) {
            List<ItemImageDTO> itemImageDTOs = new ArrayList<>();
            for (ItemImage itemImage : item.getItemImages()) {
                ItemImageDTO itemImageDTO = ItemImageDTO.builder()
                        .imageId(itemImage.getId())
                        .imageBase64(itemImage.getImageBase64())
                        .build();
                itemImageDTOs.add(itemImageDTO);
            }
            ItemWithImageDTO itemWithImageDTO = ItemWithImageDTO.builder()
                    .itemId(item.getId())
                    .itemImages(itemImageDTOs)
                    .description(item.getDescription())
                    .price(item.getPrice())
                    .title(item.getTitle())
                    .build();
            itemWithImageDTOs.add(itemWithImageDTO);
        }
        return itemWithImageDTOs;
    }

    public List<ComplaintDTO> findAllComplaints() {
        List<ComplaintDTO> complaintDTOs = new ArrayList<>();
        for (Complaint complaint : complaintRepository.findAll()) {
            ComplaintDTO complaintDTO = getComplaintDTO(complaint);
            complaintDTOs.add(complaintDTO);
        }
        return complaintDTOs;
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

    public void insertItemToCart(AddToCartDTO request) {
        User user = findUserById(request.getUserId());
        Item item = findItemById(request.getItemId());
        ItemSize itemSize = findItemSizeById(request.getItemSizeId());

        List<ItemCart> existingCartItems =
                itemCartRepository.findAllByUserAndItemAndItemSize(user, item, itemSize);

        ItemCart itemCart = new ItemCart();
        itemCart.setUser(user);
        itemCart.setItem(item);
        itemCart.setItemSize(itemSize);

        if (!existingCartItems.isEmpty()) {
            existingCartItems.forEach(existingCartItem -> {
                existingCartItem.setItemAmount(existingCartItem.getItemAmount() + request.getItemAmount());
                itemCartRepository.save(existingCartItem);
            });
        } else {
            itemCart.setItemAmount(request.getItemAmount());
            itemCartRepository.save(itemCart);
        }
    }

    public List<CartItemDTO> findCartItemsForUser(Integer userId) {
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (ItemCart itemCart : itemCartRepository.findAllByUserId(userId)) {
            CartItemDTO cartItemDTO = CartItemDTO.builder()
                    .itemsAmount(itemCart.getItemAmount())
                    .size(itemCart.getItemSize().getSize())
                    .price(itemCart.getItem().getPrice())
                    .title(itemCart.getItem().getTitle())
                    .build();
            cartItemDTOs.add(cartItemDTO);
        }
        return cartItemDTOs;
    }

    public void insertNewAddressForUser(AddressDTO dto) {
        //TODO: exception catching
        User user = userRepository.findById(dto.getUserId()).orElseThrow();

        Address address = new Address();
        address.setUser(user);
        address.setCity(dto.getCity());
        address.setStreet(dto.getStreet());
        address.setFlatNumber(dto.getFlatNumber());
        address.setPostalCode(dto.getPostalCode());
        addressRepository.save(address);
    }

    public List<AddressDTO> getAddressesForUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (Address address : user.getAddresses()) {
            AddressDTO addressDTO = AddressDTO.builder()
                    .addressId(address.getId())
                    .userId(userId)
                    .city(address.getCity())
                    .flatNumber(address.getFlatNumber())
                    .postalCode(address.getPostalCode())
                    .street(address.getStreet())
                    .build();
            addressDTOs.add(addressDTO);
        }
        return addressDTOs;
    }

    public void deleteAddress(Integer addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow();
        addressRepository.delete(address);
    }

    public void changePasswordForUser(Integer userId, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        String encodedPassword = passwordService.encodePassword(newPassword);

        if (encodedPassword != null) {
            user.setPassword(encodedPassword);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Error occurred while changing password");
        }
    }

    public void assignEmployeeToComplaint(Integer userId, Integer complaintId) {
        User user = userRepository.findById(userId).orElseThrow();
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        complaint.setUser(user);
        complaintRepository.save(complaint);

    }

    public ComplaintDTO findComplaint(Integer complaintId) {
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow();
        return getComplaintDTO(complaint);
    }

    public List<ComplaintChatMessageDTO> findComplaintChatMessages(Integer complaintId) {
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow();
        Set<ComplaintChatMessage> complaintChatMessages = complaint.getComplaintChatMessages();
        List<ComplaintChatMessageDTO> complaintChatMessageDTOs = new ArrayList<>();
        for (ComplaintChatMessage complaintChatMessage : complaintChatMessages) {

            Set<String> complaintChatImagesBase64 =
                    complaintChatMessage.getComplaintChatImages().stream()
                            .map(ComplaintChatImage::getChatImageBase64)
                            .collect(Collectors.toSet());

            ComplaintChatMessageDTO complaintChatMessageDTO = ComplaintChatMessageDTO.builder()
                    .complaintId(complaintId)
                    .chatMessageId(complaintChatMessage.getId())
                    .chatMessage(complaintChatMessage.getChatMessage())
                    .createdDate(complaintChatMessage.getCreatedDate())
                    .chatMessageUserName(complaintChatMessage.getChatMessageUserName())
                    .chatMessageUserSurname(complaintChatMessage.getChatMessageUserSurname())
                    .chatImagesBase64(complaintChatImagesBase64)
                    .build();

            complaintChatMessageDTOs.add(complaintChatMessageDTO);
        }
        return complaintChatMessageDTOs;
    }


    public void saveComplaintChatMessage(ComplaintChatMessageDTO complaintChatMessageDTO) {
        Complaint complaint = complaintRepository.findById(complaintChatMessageDTO.getComplaintId()).orElseThrow();
        ComplaintChatMessage complaintChatMessage = new ComplaintChatMessage();
        complaintChatMessage.setComplaint(complaint);
        complaintChatMessage.setChatMessage(complaintChatMessageDTO.getChatMessage());
        complaintChatMessage.setChatMessageUserName(complaintChatMessageDTO.getChatMessageUserName());
        complaintChatMessage.setChatMessageUserSurname(complaintChatMessageDTO.getChatMessageUserSurname());
        complaintChatMessage.setCreatedDate(new Date());
        complaintChatMessageRepository.save(complaintChatMessage);

        for (String chatImageBase64 : complaintChatMessageDTO.getChatImagesBase64()) {
            ComplaintChatImage complaintChatImage = new ComplaintChatImage();
            complaintChatImage.setComplaintChatMessage(complaintChatMessage);
            complaintChatImage.setChatImageBase64(chatImageBase64);
            complaintChatImageRepository.save(complaintChatImage);
        }
    }

    private ComplaintDTO getComplaintDTO(Complaint complaint) {
        User employee = complaint.getUser();
        return ComplaintDTO.builder()
                .complaintId(complaint.getId())
                .buyerId(complaint.getOrder().getId())
                .employeeId(employee != null ? employee.getId() : null)
                .buyerName(complaint.getOrder().getUser().getName())
                .buyerSurname(complaint.getOrder().getUser().getSurname())
                .complaintStatus(complaint.getComplaintStatus().getStatus())
                .employeeName(employee != null ? employee.getName() : null)
                .employeeSurname(employee != null ? employee.getSurname() : null)
                .build();
    }

    public List<OrderHistoryDTO> findAllOrdersForUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<OrderHistoryDTO> orderHistoryDTOs = new ArrayList<>();
        for (Order order : user.getOrders()) {
            OrderHistoryDTO orderHistoryDTO = OrderHistoryDTO.builder()
                    .orderId(order.getId())
                    .orderUserName(order.getUser().getName())
                    .orderUserSurname(order.getUser().getSurname())
                    .orderStatus(order.getOrderStatus().getStatus())
                    .createdDate(order.getCreatedDate())
                    .build();
            orderHistoryDTOs.add(orderHistoryDTO);
        }
        return orderHistoryDTOs;
    }

    public OrderDetailsDTO findOrderDetailsByOrderId(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Complaint complaint = order.getComplaint();
        Set<OrderDetails> ordersDetails = order.getOrdersDetails();
        Set<ItemWithImageDTO> itemWithImageDTOs = new HashSet<>();

        for (OrderDetails orderDetails : ordersDetails) {
            Item item = orderDetails.getItem();
            ItemWithImageDTO itemWithImageDTO = ItemWithImageDTO.builder()
                    .itemId(item.getId())
                    .description(item.getDescription())
                    .title(item.getTitle())
                    .price(item.getPrice())
                    .build();

            itemWithImageDTOs.add(itemWithImageDTO);
        }

        if (complaint != null) {
            ComplaintDTO complaintDTO = ComplaintDTO.builder()
                    .complaintId(complaint.getId())
                    .build();
            return OrderDetailsDTO.builder()
                    .orderId(order.getId())
                    .orderDate(order.getCreatedDate())
                    .complaint(complaintDTO)
                    .items(itemWithImageDTOs)
                    .build();
        } else {
            return OrderDetailsDTO.builder()
                    .orderId(order.getId())
                    .orderDate(order.getCreatedDate())
                    .items(itemWithImageDTOs)
                    .build();
        }
    }

    public Integer saveNewComplaint(CreateNewComplaintDTO createNewComplaintDTO) {
        Order order = orderRepository.findById(createNewComplaintDTO.getOrderId()).orElseThrow();
        User user = userRepository.findById(createNewComplaintDTO.getUserId()).orElseThrow();
        ComplaintStatus complaintStatus = complaintStatusRepository.findComplaintStatusByStatus("OPEN");
        ComplaintCategory complaintCategory = complaintCategoryRepository
                .findComplaintCategoryByCategory(createNewComplaintDTO.getComplaintCategory());
        ComplaintChatMessage complaintChatMessage = new ComplaintChatMessage();

        complaintChatMessage.setChatMessageUserName(user.getName());
        complaintChatMessage.setCreatedDate(new Date());
        complaintChatMessage.setChatMessage(createNewComplaintDTO.getComplaintMessage());
        complaintChatMessage.setChatMessageUserSurname(user.getSurname());

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setComplaintStatus(complaintStatus);
        complaint.setComplaintCategory(complaintCategory);
        complaint.setCreatedDate(new Date());
        complaint.setOrder(order);

        complaint.setComplaintChatMessages(Set.of(complaintChatMessage));
        complaintChatMessage.setComplaint(complaint);
        order.setComplaint(complaint);

        complaintRepository.save(complaint);
        complaintChatMessageRepository.save(complaintChatMessage);
        orderRepository.save(order);
        return complaint.getId();
    }
}

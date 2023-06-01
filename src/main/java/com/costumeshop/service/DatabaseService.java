package com.costumeshop.service;

import com.costumeshop.core.sql.entity.*;
import com.costumeshop.core.sql.repository.*;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.exception.DataException;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final DataMapperService dataMapperService;

    public Integer insertNewRegisteredUser(UserRegistrationDTO userRegistrationDTO) {
        try {
            UserRole userRole = userRoleRepository.findById(1).orElseThrow();
            User newUser = dataMapperService.userRegistrationDTOtoUser(userRegistrationDTO, userRole);

            String verificationToken = UUID.randomUUID().toString();
            newUser.setVerificationToken(verificationToken);

            userRepository.save(newUser);

            Address address = dataMapperService.addressDTOToUser(userRegistrationDTO, newUser);
            addressRepository.save(address);

            mailService.sendVerificationEmail(newUser);
            return newUser.getId();

        } catch (Exception e) {
            throw new DatabaseException(ErrorCode.ERR_034, e);
        }
    }

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
            logger.error(e.getMessage());
            throw new DatabaseException(ErrorCode.ERR_028, e.getMessage());
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_027, id));
    }

    public Item findItemById(Integer id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_053, id));
    }

    public List<ItemWithImageDTO> findAllItemsWithImages() {
        List<ItemWithImageDTO> itemWithImageDTOs = new ArrayList<>();
        for (Item item : itemRepository.findAll()) {
            List<ItemImageDTO> itemImageDTOs = new ArrayList<>();
            for (ItemImage itemImage : item.getItemImages()) {
                ItemImageDTO itemImageDTO = dataMapperService.itemImageToItemImageDTO(itemImage);
                itemImageDTOs.add(itemImageDTO);
            }
            ItemWithImageDTO itemWithImageDTO = dataMapperService.itemToItemWithImageDTO(item, itemImageDTOs);
            itemWithImageDTOs.add(itemWithImageDTO);
        }
        return itemWithImageDTOs;
    }

    public List<ComplaintDTO> findAllComplaints() {
        List<ComplaintDTO> complaintDTOs = new ArrayList<>();
        for (Complaint complaint : complaintRepository.findAll()) {
            ComplaintDTO complaintDTO = dataMapperService.getComplaintDTO(complaint);
            complaintDTOs.add(complaintDTO);
        }
        return complaintDTOs;
    }

    public ItemSize findItemSizeById(Integer id) {
        return itemSizeRepository.findById(id).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_054, id));
    }

    public List<ItemSize> findAllItemSizes() {
        return IterableUtils.toList(itemSizeRepository.findAll());
    }

    public List<ItemColor> findAllItemColors() {
        return IterableUtils.toList(itemColorRepository.findAll());
    }

    public void insertItemToCart(AddToCartDTO request) {
        try {
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
                    CodeMessageUtils.logMessage(InfoCode.INFO_022, user.getId(), existingCartItem.getId(), logger);
                });
            } else {
                itemCart.setItemAmount(request.getItemAmount());
                itemCartRepository.save(itemCart);
                CodeMessageUtils.logMessage(InfoCode.INFO_023, user.getId(), itemCart.getId(), logger);
            }
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    public List<CartItemDTO> findCartItemsForUser(Integer userId) {
        if (userId == null) {
            throw new DataException(ErrorCode.ERR_017);
        }
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (ItemCart itemCart : itemCartRepository.findAllByUserId(userId)) {
            CartItemDTO cartItemDTO = dataMapperService.cartItemToCartItemDTO(itemCart, userId);
            cartItemDTOs.add(cartItemDTO);
        }
        return cartItemDTOs;
    }

    public Integer insertNewAddressForUser(AddressDTO addressDTO) {
        try {
            if (addressDTO == null) {
                throw new DataException(ErrorCode.ERR_021);
            }
            if (addressDTO.getUserId() == null) {
                throw new DataException(ErrorCode.ERR_017);
            }
            User user = userRepository.findById(addressDTO.getUserId()).orElseThrow(
                    () -> new DataException(ErrorCode.ERR_027, addressDTO.getUserId()));

            Address address = dataMapperService.addressDTOToAddress(addressDTO, user);
            addressRepository.save(address);
            return address.getId();
        } catch (Exception e) {
            throw new DatabaseException(ErrorCode.ERR_030, e.getMessage());
        }
    }

    public List<AddressDTO> getAddressesForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, userId));
        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (Address address : user.getAddresses()) {
            AddressDTO addressDTO = dataMapperService.addressToAddressDTO(address, userId);
            addressDTOs.add(addressDTO);
        }
        return addressDTOs;
    }

    public void deleteAddress(Integer addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_038, addressId));
        addressRepository.delete(address);
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

    public void assignEmployeeToComplaint(Integer userId, Integer complaintId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, userId));
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_070, complaintId));
        complaint.setUser(user);
        complaintRepository.save(complaint);

    }

    public ComplaintDTO findComplaint(Integer complaintId) {
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_070, complaintId));
        return dataMapperService.getComplaintDTO(complaint);
    }

    public List<ComplaintChatMessageDTO> findComplaintChatMessages(Integer complaintId) {
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_070, complaintId));
        Set<ComplaintChatMessage> complaintChatMessages = complaint.getComplaintChatMessages();
        List<ComplaintChatMessageDTO> complaintChatMessageDTOs = new ArrayList<>();
        for (ComplaintChatMessage complaintChatMessage : complaintChatMessages) {

            Set<String> complaintChatImagesBase64 =
                    complaintChatMessage.getComplaintChatImages().stream()
                            .map(ComplaintChatImage::getChatImageBase64)
                            .collect(Collectors.toSet());

            User user = complaintChatMessage.getUser();
            UserDTO userDTO = dataMapperService.userToUserDTO(user);
            ComplaintChatMessageDTO complaintChatMessageDTO =
                    dataMapperService.complaintChatMessageToComplaintChatMessageDTO(complaintId, complaintChatMessage, complaintChatImagesBase64, userDTO);

            complaintChatMessageDTOs.add(complaintChatMessageDTO);
        }
        return complaintChatMessageDTOs;
    }

    public Integer saveComplaintChatMessage(ComplaintChatMessageDTO complaintChatMessageDTO) {
        User user = userRepository.findById(complaintChatMessageDTO.getUser().getId()).orElseThrow(
                () -> new DatabaseException(ErrorCode.ERR_027, complaintChatMessageDTO.getUser().getId())
        );
        Complaint complaint = complaintRepository.findById(complaintChatMessageDTO.getComplaintId()).orElseThrow(
                () -> new DatabaseException(ErrorCode.ERR_070, complaintChatMessageDTO.getComplaintId())
        );
        ComplaintChatMessage complaintChatMessage =
                dataMapperService.complaintChatMessageDTOToComplaintChatMessage(complaintChatMessageDTO, user, complaint);
        complaintChatMessageRepository.save(complaintChatMessage);

        for (String chatImageBase64 : complaintChatMessageDTO.getChatImagesBase64()) {
            ComplaintChatImage complaintChatImage = new ComplaintChatImage();
            complaintChatImage.setComplaintChatMessage(complaintChatMessage);
            complaintChatImage.setChatImageBase64(chatImageBase64);
            complaintChatImageRepository.save(complaintChatImage);
        }
        return complaintChatMessage.getId();
    }

    public List<OrderDTO> findAllOrdersForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, userId));
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : user.getOrders()) {
            OrderDTO orderDTO = dataMapperService.orderToOrderDTO(order, user);
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }

    public OrderDetailsDTO findOrderDetailsByOrderId(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_081, orderId));
        Complaint complaint = order.getComplaint();
        Set<OrderDetails> ordersDetails = order.getOrdersDetails();
        if (ordersDetails.isEmpty()) {
            throw new DataException(ErrorCode.ERR_082, orderId);
        }
        Set<ItemWithImageDTO> itemWithImageDTOs = new HashSet<>();

        List<ItemImageDTO> itemImageDTOs = new ArrayList<>();

        for (OrderDetails orderDetails : ordersDetails) {
            Item item = orderDetails.getItem();
            for (ItemImage itemImage : item.getItemImages()) {
                ItemImageDTO itemImageDTO = dataMapperService.itemImageToItemImageDTO(itemImage);
                itemImageDTOs.add(itemImageDTO);
            }
            ItemWithImageDTO itemWithImageDTO =
                    dataMapperService.itemToItemWithImageDTO(item, itemImageDTOs);

            itemWithImageDTOs.add(itemWithImageDTO);
        }

        if (complaint != null) {
            ComplaintDTO complaintDTO = ComplaintDTO.builder()
                    .complaintId(complaint.getId())
                    .build();
            return dataMapperService.orderToOrderDetailsDTO(order, itemWithImageDTOs, complaintDTO);
        } else {
            return OrderDetailsDTO.builder()
                    .orderId(order.getId())
                    .orderDate(order.getCreatedDate())
                    .items(itemWithImageDTOs)
                    .build();
        }
    }

    public Integer saveNewComplaint(CreateNewComplaintDTO createNewComplaintDTO) {
        Order order = orderRepository.findById(createNewComplaintDTO.getOrderId())
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_081, createNewComplaintDTO.getOrderId()));
        User user = userRepository.findById(createNewComplaintDTO.getUserId())
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, createNewComplaintDTO.getUserId()));
        ComplaintStatus complaintStatus = complaintStatusRepository.findComplaintStatusByStatus("OPEN");
        if (complaintStatus == null) {
            throw new DatabaseException(ErrorCode.ERR_086, "OPEN");
        }
        ComplaintCategory complaintCategory = complaintCategoryRepository
                .findComplaintCategoryByCategory(createNewComplaintDTO.getComplaintCategory());
        if (complaintCategory == null) {
            throw new DatabaseException(ErrorCode.ERR_087, createNewComplaintDTO.getComplaintCategory());
        }

        ComplaintChatMessage complaintChatMessage = new ComplaintChatMessage();
        complaintChatMessage.setUser(user);
        complaintChatMessage.setCreatedDate(new Date());
        complaintChatMessage.setChatMessage(createNewComplaintDTO.getComplaintMessage());

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

    public List<OrderDTO> findAllOrders() {
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            User user = order.getUser();
            OrderDTO orderDTO = dataMapperService.orderToOrderDTO(order, user);
            orderDTOs.add(orderDTO);
        }

        return orderDTOs;
    }
}

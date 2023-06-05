package com.costumeshop.service.database;

import com.costumeshop.core.sql.entity.*;
import com.costumeshop.core.sql.repository.*;
import com.costumeshop.exception.DataException;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.model.dto.*;
import com.costumeshop.service.DataMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDatabaseService {
    private final UserDatabaseService userDatabaseService;
    private final AddressDatabaseService addressDatabaseService;
    private final ItemDatabaseService itemDatabaseService;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final DataMapperService dataMapperService;

    public Order findOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_081, orderId));
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

    public List<OrderDTO> findAllOrdersByUserId(Integer userId) {
        User user = userDatabaseService.findUserById(userId);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : user.getOrders()) {
            OrderDTO orderDTO = dataMapperService.orderToOrderDTO(order, user);
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }

    public OrderDetailsDTO findOrderDetailsByOrderId(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_081, orderId));
        Complaint complaint = order.getComplaint();
        Set<OrderDetails> ordersDetails = order.getOrdersDetails();
        if (ordersDetails.isEmpty()) {
            throw new DataException(ErrorCode.ERR_082, orderId);
        }
        Set<ItemDTO> itemDTOs = new HashSet<>();

        List<ItemImageDTO> itemImageDTOs = new ArrayList<>();

        for (OrderDetails orderDetails : ordersDetails) {
            Item item = orderDetails.getItem();
            for (ItemImage itemImage : item.getItemImages()) {
                ItemImageDTO itemImageDTO = dataMapperService.itemImageToItemImageDTO(itemImage);
                itemImageDTOs.add(itemImageDTO);
            }
            ItemDTO itemDTO =
                    dataMapperService.itemToItemDTO(item, itemImageDTOs);

            itemDTOs.add(itemDTO);
        }

        if (complaint != null) {
            ComplaintDTO complaintDTO = ComplaintDTO.builder()
                    .complaintId(complaint.getId())
                    .build();
            return dataMapperService.orderToOrderDetailsDTO(order, itemDTOs, complaintDTO);
        } else {
            return OrderDetailsDTO.builder()
                    .orderId(order.getId())
                    .orderDate(order.getCreatedDate())
                    .items(itemDTOs)
                    .buyerId(order.getUser().getId())
                    .orderStatus(order.getOrderStatus().getStatus())
                    .build();
        }
    }

    public UserDTO findUserByOrderId(Integer orderId) {
        if (orderId == null) {
            throw new DataException(ErrorCode.ERR_061);
        }

        Order order = findOrderById(orderId);
        User user = order.getUser();
        return dataMapperService.userToUserDTO(user);
    }

    public Order insertNewOrderByOrderDTO(CartConfirmationDTO cartConfirmationDTO) {
        User user = userDatabaseService.findUserById(cartConfirmationDTO.getUserId());
        Integer addressId = cartConfirmationDTO.getAddress().getAddressId();
        Address address = addressDatabaseService.findAddressById(addressId);
        OrderStatus orderStatus = orderStatusRepository.findByStatus("NEW");

        Order order = dataMapperService.orderDTOToOrder(user, address, orderStatus);
        orderRepository.save(order);

        insertNewOrderDetailsByCartConfirmationDTOAndOrder(cartConfirmationDTO, order);
        return order;
    }

    public void insertNewOrder(Order order) {
        orderRepository.save(order);
    }

    public PaymentTransaction saveNewPaymentTransaction(PaymentTransactionDTO paymentTransactionDTO) {
        if (paymentTransactionDTO == null) {
            throw new DataException(ErrorCode.ERR_098);
        }
        if (paymentTransactionDTO.getUserId() == null) {
            throw new DataException(ErrorCode.ERR_014);
        }
        if (paymentTransactionDTO.getPaidAmount() == null) {
            throw new DataException(ErrorCode.ERR_099);
        }
        Order order = orderRepository.findById(paymentTransactionDTO.getOrderId())
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, paymentTransactionDTO.getUserId()));
        String paymentStatusStatus = "PENDING";
        PaymentStatus paymentStatus = paymentStatusRepository.findByStatus(paymentStatusStatus)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_102, paymentStatusStatus));
        PaymentTransaction paymentTransaction =
                dataMapperService.paymentTransactionDTOToPaymentTransaction(paymentTransactionDTO, order, paymentStatus);
        paymentTransactionRepository.save(paymentTransaction);
        return paymentTransaction;
    }


    private void insertNewOrderDetailsByCartConfirmationDTOAndOrder(CartConfirmationDTO cartConfirmationDTO, Order order) {
        List<CartItemDTO> cartItemDTOs = cartConfirmationDTO.getCartItems();
        cartItemDTOs.forEach(cartItemDTO -> {
                    deductItemQuantity(cartItemDTO);
                    cartItemDTO.getItems().forEach(itemDTO -> {

                        Integer itemId = itemDTO.getItemId();
                        Item item = itemDatabaseService.findItemById(itemId);

                        String itemSizeSize = cartItemDTO.getSize();
                        ItemSize itemSize = itemDatabaseService.findItemSizeBySize(itemSizeSize);

                        OrderDetails orderDetails = new OrderDetails();
                        orderDetails.setOrder(order);
                        orderDetails.setItem(item);
                        orderDetails.setItemSize(itemSize);
                        orderDetailsRepository.save(orderDetails);
                    });
                }

        );
    }


    public List<OrderStatusDTO> findAllOrderStatuses() {
        List<OrderStatusDTO> orderStatusDTOs = new ArrayList<>();
        orderStatusRepository.findAll().forEach(orderStatus -> {
            OrderStatusDTO orderStatusDTO = OrderStatusDTO.builder()
                    .orderStatusId(orderStatus.getId())
                    .status(orderStatus.getStatus())
                    .build();
            orderStatusDTOs.add(orderStatusDTO);
        });
        return orderStatusDTOs;
    }

    public void updateOrderIdByOrderStatusDTO(OrderStatusDTO orderStatusDTO) {
        if (orderStatusDTO.getOrderId() == null) {
            throw new DataException(ErrorCode.ERR_059);
        }
        Order order = findOrderById(Integer.valueOf(orderStatusDTO.getOrderId()));
        OrderStatus orderStatus = orderStatusRepository.findByStatus(orderStatusDTO.getStatus());
        order.setOrderStatus(orderStatus);
        order.setLastModifiedDate(new Date());
        insertNewOrder(order);
    }

    private void deductItemQuantity(CartItemDTO cartItemDTO) {
        Integer quantityToBeDeducted = cartItemDTO.getItems().size();
        ItemDTO itemDTO = cartItemDTO.getItems().get(0);
        Integer quantityAfterDeduction = itemDTO.getQuantity() - quantityToBeDeducted;
        if (quantityAfterDeduction < 0) {
            throw new DataException(ErrorCode.ERR_117);
        }

        Item item = itemDatabaseService.findItemById(itemDTO.getItemId());
        item.setQuantity(quantityAfterDeduction);
        itemDatabaseService.insertItem(item);
        itemDTO.setQuantity(quantityAfterDeduction);
    }
}

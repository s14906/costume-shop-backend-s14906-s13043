package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.Order;
import com.costumeshop.core.sql.entity.PaymentTransaction;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.*;
import com.costumeshop.model.response.*;
import com.costumeshop.service.database.OrderDatabaseService;
import com.costumeshop.service.database.CartDatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin()
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderDatabaseService orderDatabaseService;
    private final CartDatabaseService cartDatabaseService;

    @GetMapping(path = "/orders")
    public @ResponseBody ResponseEntity<?> getAllOrders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<OrderDTO> orderDTOs = orderDatabaseService.findAllOrders();
            CodeMessageUtils.logMessage(InfoCode.INFO_027, logger);
            return new ResponseEntity<>(OrderResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_027))
                    .orders(orderDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_062, e, logger);
            return new ResponseEntity<>(OrderResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_062))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/orders/statuses")
    public @ResponseBody ResponseEntity<?> getAllOrderStatuses() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<OrderStatusDTO> orderStatusDTOs = orderDatabaseService.findAllOrderStatuses();
            CodeMessageUtils.logMessage(InfoCode.INFO_062, logger);
            return new ResponseEntity<>(OrderStatusResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_062))
                    .orderStatuses(orderStatusDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_122, e, logger);
            return new ResponseEntity<>(OrderResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_122))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/users/{userId}/orders")
    public @ResponseBody ResponseEntity<?> getOrdersForUser(@PathVariable("userId") Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<OrderDTO> orderDTOs = orderDatabaseService.findAllOrdersByUserId(userId);
            CodeMessageUtils.logMessage(InfoCode.INFO_036, userId, logger);
            return new ResponseEntity<>(OrderResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_036, userId))
                    .orders(orderDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_080, userId, e, logger);
            return new ResponseEntity<>(OrderResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_080, userId))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/orders/{orderId}")
    public @ResponseBody ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Integer orderId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            OrderDetailsDTO orderDetailsDTO = orderDatabaseService.findOrderDetailsByOrderId(orderId);
            CodeMessageUtils.logMessage(InfoCode.INFO_037, orderId, logger);
            return new ResponseEntity<>(OrderDetailsResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_037, orderId))
                    .orderDetails(orderDetailsDTO)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_085, orderId, e, logger);
            return new ResponseEntity<>(OrderDetailsResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_085, orderId))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/orders/{orderId}/user")
    public @ResponseBody ResponseEntity<?> getUserByOrderId(@PathVariable("orderId") Integer orderId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            UserDTO userDTO = orderDatabaseService.findUserByOrderId(orderId);
            CodeMessageUtils.logMessage(InfoCode.INFO_064, orderId, logger);
            return new ResponseEntity<>(UserResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_064, orderId))
                    .user(userDTO)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_124, orderId, e, logger);
            return new ResponseEntity<>(UserResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_124, orderId))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/orders")
    public @ResponseBody ResponseEntity<?> postUpdateOrderStatusForOrder(@RequestBody OrderStatusDTO orderStatusDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        Integer orderId = Integer.valueOf(orderStatusDTO.getOrderId());
        try {
            orderDatabaseService.updateOrderIdByOrderStatusDTO(orderStatusDTO);
            CodeMessageUtils.logMessage(InfoCode.INFO_063, orderId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_063, orderId))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_123, orderId, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_123, orderId))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/orders/payment")
    @Transactional
    public @ResponseBody ResponseEntity<?> postCreateNewOrderPaymentTransaction(@RequestBody CartConfirmationDTO cartConfirmationDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        Integer userId = cartConfirmationDTO.getUserId();
        PaymentTransaction paymentTransaction;
        Order order;
        try {
            order = orderDatabaseService.insertNewOrderByOrderDTO(cartConfirmationDTO);
            CodeMessageUtils.logMessage(InfoCode.INFO_049, order.getId(), logger);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_096, cartConfirmationDTO.getUserId(), e, logger);
            return new ResponseEntity<>(OrderResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_097))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            cartDatabaseService.deleteCartItemsByUserId(userId);
            CodeMessageUtils.logMessage(InfoCode.INFO_045, userId, logger);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_093, userId, e, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_094))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        PaymentTransactionDTO paymentTransactionDTO = PaymentTransactionDTO.builder()
                .userId(userId)
                .orderId(order.getId())
                .paidAmount(cartConfirmationDTO.getPaidAmount())
                .build();

        try {
            paymentTransaction = orderDatabaseService.saveNewPaymentTransaction(paymentTransactionDTO);
            CodeMessageUtils.logMessage(InfoCode.INFO_051, paymentTransaction.getId(),
                    paymentTransactionDTO.getUserId(), logger);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_100,
                    paymentTransactionDTO.getUserId(), e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_101))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(PaymentTransactionResponse.builder()
                .success(true)
                .paymentTransactionId(paymentTransaction.getId())
                .message(CodeMessageUtils.getMessage(InfoCode.INFO_052))
                .build(), responseHeaders, HttpStatus.OK);
    }
}

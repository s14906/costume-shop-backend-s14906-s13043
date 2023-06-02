package com.costumeshop.controller;

import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.CreateNewComplaintDTO;
import com.costumeshop.model.dto.OrderDTO;
import com.costumeshop.model.dto.OrderDetailsDTO;
import com.costumeshop.model.response.ComplaintResponse;
import com.costumeshop.model.response.OrderDetailsResponse;
import com.costumeshop.model.response.OrderResponse;
import com.costumeshop.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin()
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final DatabaseService databaseService;

    @GetMapping(path = "/orders")
    public @ResponseBody ResponseEntity<?> getAllOrders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<OrderDTO> orderDTOs = databaseService.findAllOrders();
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
                    .build(), responseHeaders, HttpStatus.OK);
        }

    }

    @GetMapping(path = "/users/{userId}/orders")
    public @ResponseBody ResponseEntity<?> getOrdersForUser(@PathVariable("userId") Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<OrderDTO> orderDTOs = databaseService.findAllOrdersForUser(userId);
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
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/orders/{orderId}")
    public @ResponseBody ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Integer orderId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            OrderDetailsDTO orderDetailsDTO = databaseService.findOrderDetailsByOrderId(orderId);
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
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @PostMapping(path = "/complaints/create")
    public @ResponseBody ResponseEntity<?> createNewComplaint(
            @RequestBody CreateNewComplaintDTO createNewComplaintDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            Integer complaintId = databaseService.saveNewComplaint(createNewComplaintDTO);
            CodeMessageUtils.logMessage(InfoCode.INFO_039, complaintId, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_038))
                    .complaintId(complaintId)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_088, e, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_088))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

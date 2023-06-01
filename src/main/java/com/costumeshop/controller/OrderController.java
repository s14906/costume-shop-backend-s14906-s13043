package com.costumeshop.controller;

import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.*;
import com.costumeshop.model.response.*;
import com.costumeshop.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    @GetMapping(path = "/complaints")
    public @ResponseBody ResponseEntity<?> getAllComplaints() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ComplaintDTO> complaintDTOs = databaseService.findAllComplaints();
            CodeMessageUtils.logMessage(InfoCode.INFO_028, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_028))
                    .complaints(complaintDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_069, e, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_069))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/complaints/{complaintId}")
    public @ResponseBody ResponseEntity<?> getComplaint(@PathVariable("complaintId") Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            ComplaintDTO complaintDTO = databaseService.findComplaint(complaintId);
            CodeMessageUtils.logMessage(InfoCode.INFO_029, complaintId, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_030))
                    .complaints(Collections.singletonList(complaintDTO))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_071, complaintId, e, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_071, complaintId))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/complaints/{complaintId}/messages")
    public @ResponseBody ResponseEntity<?> getComplaintChatMessages(@PathVariable("complaintId") Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ComplaintChatMessageDTO> complaintChatMessageDTOs = databaseService.findComplaintChatMessages(complaintId);
            CodeMessageUtils.logMessage(InfoCode.INFO_031, complaintId, logger);
            return new ResponseEntity<>(ComplaintChatMessageResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_031, complaintId))
                    .complaintChatMessages(complaintChatMessageDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_075, complaintId, e, logger);
            return new ResponseEntity<>(ComplaintChatMessageResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_075, complaintId))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @PostMapping(path = "/complaints/{complaintId}/messages")
    public @ResponseBody ResponseEntity<?> saveComplaintChatMessage(
            @RequestBody ComplaintChatMessageDTO complaintChatMessageDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            Integer complaintChatMessageId = databaseService.saveComplaintChatMessage(complaintChatMessageDTO);
            CodeMessageUtils.logMessage(InfoCode.INFO_033, complaintChatMessageDTO.getUser().getId(),
                    complaintChatMessageId, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_032))
                    .complaintId(complaintChatMessageDTO.getComplaintId())
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_077, complaintChatMessageDTO.getUser().getId(), e, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_077, complaintChatMessageDTO.getUser().getId()))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/complaints")
    public @ResponseBody ResponseEntity<?> assignEmployeeToComplaint(@RequestParam Integer userId,
                                                                     @RequestParam Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.assignEmployeeToComplaint(userId, complaintId);
            CodeMessageUtils.logMessage(InfoCode.INFO_035, complaintId, userId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_034))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_079, userId, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_078))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
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

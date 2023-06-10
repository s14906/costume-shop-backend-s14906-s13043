package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.Complaint;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.ComplaintChatMessageDTO;
import com.costumeshop.model.dto.ComplaintDTO;
import com.costumeshop.model.dto.CreateNewComplaintDTO;
import com.costumeshop.model.response.ComplaintChatMessageResponse;
import com.costumeshop.model.response.ComplaintResponse;
import com.costumeshop.model.response.SimpleResponse;
import com.costumeshop.service.DataMapperService;
import com.costumeshop.service.database.ComplaintDatabaseService;
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
public class ComplaintController {
    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);
    private final ComplaintDatabaseService complaintDatabaseService;
    private final DataMapperService dataMapperService;

    @GetMapping(path = "/complaints")
    public @ResponseBody ResponseEntity<?> getAllComplaints() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ComplaintDTO> complaintDTOs = complaintDatabaseService.findAllComplaints();
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
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/complaints/{complaintId}")
    public @ResponseBody ResponseEntity<?> getComplaintById(@PathVariable("complaintId") Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            ComplaintDTO complaintDTO = complaintDatabaseService.findComplaintDTOById(complaintId);
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
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/complaints/{complaintId}/messages")
    public @ResponseBody ResponseEntity<?> getComplaintChatMessagesByComplaintId(@PathVariable("complaintId") Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ComplaintChatMessageDTO> complaintChatMessageDTOs = complaintDatabaseService.findComplaintChatMessagesByComplaintId(complaintId);
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
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/complaints/{complaintId}/messages")
    public @ResponseBody ResponseEntity<?> postSendComplaintChatMessage(
            @RequestBody ComplaintChatMessageDTO complaintChatMessageDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            Integer complaintChatMessageId = complaintDatabaseService.insertComplaintChatMessageByComplaintChatMessageDTO(complaintChatMessageDTO);
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
    public @ResponseBody ResponseEntity<?> postAssignComplaintToEmployee(@RequestParam Integer userId,
                                                                         @RequestParam Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            complaintDatabaseService.assignEmployeeToComplaint(userId, complaintId);
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

    @PostMapping(path = "/complaints/create")
    public @ResponseBody ResponseEntity<?> postCreateNewComplaint(
            @RequestBody CreateNewComplaintDTO createNewComplaintDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            Integer complaintId = complaintDatabaseService.insertNewComplaintByCreateNewComplaintDTO(createNewComplaintDTO);
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

    @PostMapping(path = "/complaints/{complaintId}")
    public @ResponseBody ResponseEntity<?> psotCloseComplaintById(@PathVariable("complaintId") Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            String closedStatus = "CLOSED";
            Complaint complaint = complaintDatabaseService.updateComplaintStatus(complaintId);
            ComplaintDTO complaintDTO = dataMapperService.complaintToComplaintDTO(complaint);
            CodeMessageUtils.logMessage(InfoCode.INFO_061, closedStatus, complaintId, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_061, closedStatus, complaintId))
                    .complaints(Collections.singletonList(complaintDTO))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_120, complaintId, e, logger);
            return new ResponseEntity<>(ComplaintResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_120, complaintId))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

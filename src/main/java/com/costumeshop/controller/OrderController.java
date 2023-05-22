package com.costumeshop.controller;

import com.costumeshop.model.dto.ComplaintDTO;
import com.costumeshop.model.response.SimpleResponse;
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

    @GetMapping(path = "/complaints")
    public List<ComplaintDTO> getAllComplaints() {
        return databaseService.findAllComplaints();
    }

    @GetMapping(path = "/complaints/{complaintId}")
    public ComplaintDTO getComplaint(@PathVariable("complaintId") Integer complaintId) {
        return databaseService.findComplaint(complaintId);
    }

    @PostMapping(path = "/complaints")
    public @ResponseBody ResponseEntity<?> assignEmployeeToComplaint(@RequestParam Integer userId,
                                                                     @RequestParam Integer complaintId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.assignEmployeeToComplaint(userId, complaintId);
        } catch (Exception e) {
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message("Error occurred when assigning complaint to the employee!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(SimpleResponse.builder()
                .success(true)
                .message("Complaint assigned successfully!")
                .build(), responseHeaders, HttpStatus.OK);
    }
}

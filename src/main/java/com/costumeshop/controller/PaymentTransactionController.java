package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.User;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.CartConfirmationDTO;
import com.costumeshop.model.response.SimpleResponse;
import com.costumeshop.service.MailService;
import com.costumeshop.service.database.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin()
public class PaymentTransactionController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTransactionController.class);
    private final UserDatabaseService userDatabaseService;
    private final MailService mailService;
    @PostMapping("/email")
    public @ResponseBody ResponseEntity<?> postSendPaymentTransactionSuccessEmail(
            @RequestBody CartConfirmationDTO cartConfirmationDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            User user = userDatabaseService.findUserById(cartConfirmationDTO.getUserId());
            mailService.sendPaymentTransactionSuccessEmail(user, cartConfirmationDTO.getCartItems());
            CodeMessageUtils.logMessage(InfoCode.INFO_060, user.getId(), logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_060, user.getId()))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_119, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_119, e))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

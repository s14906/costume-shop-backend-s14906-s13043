package com.costumeshop.controller;

import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.AddToCartDTO;
import com.costumeshop.model.dto.CartItemDTO;
import com.costumeshop.model.response.CartResponse;
import com.costumeshop.model.response.SimpleResponse;
import com.costumeshop.service.database.CartDatabaseService;
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
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartDatabaseService cartDatabaseService;

    @PostMapping(path = "/cart")
    public @ResponseBody ResponseEntity<?> postAddToCart(@RequestBody AddToCartDTO request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            cartDatabaseService.insertCartItemByAddToCartDTO(request);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_024))
                    .build(), responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_055, request.getUserId(), e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_056))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/user/{userId}/cart")
    public @ResponseBody ResponseEntity<?> getCartByUserId(@PathVariable Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<CartItemDTO> cartItemDTOs = cartDatabaseService.findCartItemsByUserId(userId);
            CodeMessageUtils.logMessage(InfoCode.INFO_026, userId, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_025))
                    .cartItems(cartItemDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_057, userId, e, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_058))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/user/{userId}/cart/items")
    public @ResponseBody ResponseEntity<?> deleteCartByUserId(@PathVariable Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            cartDatabaseService.deleteCartItemsByUserId(userId);
            CodeMessageUtils.logMessage(InfoCode.INFO_045, userId, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_046))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_093, userId, e, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_094))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/user/{userId}/cart/items/{cartItemId}")
    public @ResponseBody ResponseEntity<?> deleteCartItemByUserIdAndCartItemId(@PathVariable Integer userId,
                                                                               @PathVariable Integer cartItemId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            cartDatabaseService.deleteCartItemByCartItemId(cartItemId);
            CodeMessageUtils.logMessage(InfoCode.INFO_047, cartItemId, userId, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_048))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_093, userId, e, logger);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_094))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

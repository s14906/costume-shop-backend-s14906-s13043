package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.ItemColor;
import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.AddToCartDTO;
import com.costumeshop.model.dto.CartItemDTO;
import com.costumeshop.model.dto.ItemWithImageDTO;
import com.costumeshop.model.response.CartResponse;
import com.costumeshop.model.response.ItemResponse;
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
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final DatabaseService databaseService;

    @GetMapping(path = "/items")
    public @ResponseBody ResponseEntity<?> getAllItemsWithImages() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemWithImageDTO> itemWithImageDTOs = databaseService.findAllItemsWithImages();

            CodeMessageUtils.logMessage(InfoCode.INFO_011, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_011))
                    .itemsWithImages(itemWithImageDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_051, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_051))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/items/{searchText}")
    public @ResponseBody ResponseEntity<?> getAllItemsWithImagesBySearchText(@PathVariable String searchText) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemWithImageDTO> itemWithImageDTOs = databaseService.findAllItemsWithImagesBySearchText(searchText);
            CodeMessageUtils.logMessage(InfoCode.INFO_043, searchText, itemWithImageDTOs.size(), logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_044))
                    .itemsWithImages(itemWithImageDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_092, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_051))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/items/sizes")
    public @ResponseBody ResponseEntity<?> getAllItemSizes() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemSize> itemSizes = databaseService.findAllItemSizes();

            CodeMessageUtils.logMessage(InfoCode.INFO_021, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_021))
                    .itemSizes(itemSizes)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_052, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_052))
                    .build(), responseHeaders, HttpStatus.OK);
        }
    }

    //TODO: delete this
    @GetMapping(path = "/items/colors")
    public List<ItemColor> getAllItemColors() {
        return databaseService.findAllItemColors();
    }

    @PostMapping(path = "/cart")
    public @ResponseBody ResponseEntity<?> addToCart(@RequestBody AddToCartDTO request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            databaseService.insertItemToCart(request);
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

    @GetMapping(path = "/cart")
    public @ResponseBody ResponseEntity<?> getCartForUser(@RequestParam Integer userId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<CartItemDTO> cartItemDTOs = databaseService.findCartItemsForUser(userId);
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
}

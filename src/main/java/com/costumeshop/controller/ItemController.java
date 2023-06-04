package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.ItemColor;
import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.ItemCategoryDTO;
import com.costumeshop.model.dto.ItemDTO;
import com.costumeshop.model.dto.ItemSetDTO;
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

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin()
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final DatabaseService databaseService;

    @GetMapping(path = "/items")
    public @ResponseBody ResponseEntity<?> getAllItems() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemDTO> itemDTOs = databaseService.findAllItems();

            CodeMessageUtils.logMessage(InfoCode.INFO_011, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_011))
                    .items(itemDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_051, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_051))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/items/categories")
    public @ResponseBody ResponseEntity<?> getAllItemCategories() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemCategoryDTO> itemCategoryDTOs = databaseService.findAllItemCategories();

            CodeMessageUtils.logMessage(InfoCode.INFO_057, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_057))
                    .itemCategories(itemCategoryDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_111, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_111))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/items/sets")
    public @ResponseBody ResponseEntity<?> getAllItemSets() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemSetDTO> itemSetDTOs = databaseService.findAllItemSets();

            CodeMessageUtils.logMessage(InfoCode.INFO_058, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_058))
                    .itemSets(itemSetDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_111, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_111))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/items/{itemId}")
    public @ResponseBody ResponseEntity<?> getItemById(@PathVariable Integer itemId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            ItemDTO itemDTO = databaseService.getItemDTOById(itemId);

            CodeMessageUtils.logMessage(InfoCode.INFO_011, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_011))
                    .items(Collections.singletonList(itemDTO))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_051, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_051))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/items")
    public @ResponseBody ResponseEntity<?> postItem(@RequestBody ItemDTO itemDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            Integer itemId = databaseService.insertItem(itemDTO);
            CodeMessageUtils.logMessage(InfoCode.INFO_055, itemId, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_056))
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_108, e, logger);
            return new ResponseEntity<>(SimpleResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_108))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/search/{searchText}")
    public @ResponseBody ResponseEntity<?> getAllItemsBySearchText(@PathVariable String searchText) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<ItemDTO> itemDTOs = databaseService.findAllItemsWithImagesBySearchText(searchText);
            CodeMessageUtils.logMessage(InfoCode.INFO_043, searchText, itemDTOs.size(), logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(true)
                    .message(CodeMessageUtils.getMessage(InfoCode.INFO_044))
                    .items(itemDTOs)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            CodeMessageUtils.logMessageAndPrintStackTrace(ErrorCode.ERR_092, e, logger);
            return new ResponseEntity<>(ItemResponse.builder()
                    .success(false)
                    .message(CodeMessageUtils.getMessage(ErrorCode.ERR_051))
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
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
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: delete this
    @GetMapping(path = "/items/colors")
    public List<ItemColor> getAllItemColors() {
        return databaseService.findAllItemColors();
    }

}

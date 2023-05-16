package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.Item;
import com.costumeshop.core.sql.entity.ItemCart;
import com.costumeshop.core.sql.entity.ItemColor;
import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.model.request.AddToCartRequest;
import com.costumeshop.model.response.AddToCartResponse;
import com.costumeshop.model.response.CartResponse;
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
    public List<Item> getAllItems() {
        return databaseService.findAllItems();
    }

    @GetMapping(path = "/items/sizes")
    public List<ItemSize> getAllItemSizes() {
        return databaseService.findAllItemSizes();
    }

    @GetMapping(path = "/items/colors")
    public List<ItemColor> getAllItemColors() {
        return databaseService.findAllItemColors();
    }

    @PostMapping(path = "/cart")
    public @ResponseBody ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        try {
            databaseService.insertItemToCart(request);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(AddToCartResponse.builder()
                    .success(true)
                    .message("Item added to cart!")
                    .build(), responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Failed to add item to cart: " + e.getMessage());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(AddToCartResponse.builder()
                    .success(false)
                    .message("Failed to add item to cart!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/cart")
    public @ResponseBody ResponseEntity<?> getCartForUser(@RequestParam Integer userId) {
        try {
            List<ItemCart> cartItems = databaseService.findCartItemsForUser(userId);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(CartResponse.builder()
                    .success(true)
                    .message("Items retrieved successfully!")
                    .cartItems(cartItems)
                    .build(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(CartResponse.builder()
                    .success(false)
                    .message("Failed to retrieve items from cart!")
                    .build(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

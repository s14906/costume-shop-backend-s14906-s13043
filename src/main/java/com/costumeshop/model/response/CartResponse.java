package com.costumeshop.model.response;

import com.costumeshop.model.dto.CartItemDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CartResponse extends AbstractResponse {
    private final List<CartItemDTO> cartItems;


    @Builder
    public CartResponse(boolean success, String message, List<CartItemDTO> cartItems) {
        super(success, message);
        this.cartItems = cartItems;
    }
}

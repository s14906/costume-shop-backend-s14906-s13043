package com.costumeshop.model.response;

import com.costumeshop.core.sql.entity.ItemCart;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CartResponse extends AbstractResponse {
    private final List<ItemCart> cartItems;


    @Builder
    public CartResponse(boolean success, String message, List<ItemCart> cartItems) {
        super(success, message);
        this.cartItems = cartItems;
    }
}

package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartConfirmationDTO {
    private Integer userId;
    private BigDecimal paidAmount;
    private AddressDTO address;
    private List<CartItemDTO> cartItems;
}

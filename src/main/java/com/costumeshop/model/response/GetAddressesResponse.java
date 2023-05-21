package com.costumeshop.model.response;

import com.costumeshop.model.dto.AddressDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetAddressesResponse extends AbstractResponse {
    private final List<AddressDTO> addresses;

    @Builder
    public GetAddressesResponse(boolean success, String message, List<AddressDTO> addresses) {
        super(success, message);
        this.addresses = addresses;
    }
}

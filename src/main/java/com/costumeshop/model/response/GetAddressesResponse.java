package com.costumeshop.model.response;

import com.costumeshop.core.sql.entity.Address;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class GetAddressesResponse extends AbstractResponse {
    private final Set<Address> addresses;

    @Builder
    public GetAddressesResponse(boolean success, String message, Set<Address> addresses) {
        super(success, message);
        this.addresses = addresses;
    }
}

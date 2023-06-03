package com.costumeshop.model.response;

import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.model.dto.ItemDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ItemResponse extends AbstractResponse {
    private final List<ItemDTO> itemsWithImages;
    private final List<ItemSize> itemSizes;

    @Builder
    public ItemResponse(boolean success, String message, List<ItemDTO> itemsWithImages, List<ItemSize> itemSizes) {
        super(success, message);
        this.itemsWithImages = itemsWithImages;
        this.itemSizes = itemSizes;
    }
}

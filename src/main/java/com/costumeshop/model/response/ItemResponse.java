package com.costumeshop.model.response;

import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.model.dto.ItemWithImageDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ItemResponse extends AbstractResponse {
    private final List<ItemWithImageDTO> itemsWithImages;
    private final List<ItemSize> itemSizes;

    @Builder
    public ItemResponse(boolean success, String message, List<ItemWithImageDTO> itemsWithImages, List<ItemSize> itemSizes) {
        super(success, message);
        this.itemsWithImages = itemsWithImages;
        this.itemSizes = itemSizes;
    }
}

package com.costumeshop.model.response;

import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.model.dto.ItemCategoryDTO;
import com.costumeshop.model.dto.ItemDTO;
import com.costumeshop.model.dto.ItemSetDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ItemResponse extends AbstractResponse {
    private final List<ItemDTO> items;
    private final List<ItemSize> itemSizes;
    private final List<ItemCategoryDTO> itemCategories;
    private final List<ItemSetDTO> itemSets;

    @Builder
    public ItemResponse(boolean success, String message, List<ItemDTO> items, List<ItemSize> itemSizes,
                        List<ItemCategoryDTO> itemCategories, List<ItemSetDTO> itemSets) {
        super(success, message);
        this.items = items;
        this.itemSizes = itemSizes;
        this.itemCategories = itemCategories;
        this.itemSets = itemSets;
    }
}

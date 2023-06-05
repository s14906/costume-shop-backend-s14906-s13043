package com.costumeshop.service.database;

import com.costumeshop.core.sql.entity.*;
import com.costumeshop.core.sql.repository.*;
import com.costumeshop.exception.DataException;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.model.dto.ItemCategoryDTO;
import com.costumeshop.model.dto.ItemDTO;
import com.costumeshop.model.dto.ItemImageDTO;
import com.costumeshop.model.dto.ItemSetDTO;
import com.costumeshop.service.DataMapperService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemDatabaseService {
    private final DataMapperService dataMapperService;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemSizeRepository itemSizeRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ItemSetRepository itemSetRepository;

    public Item findItemById(Integer id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_053, id));
    }

    public List<ItemDTO> findAllItems() {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        for (Item item : itemRepository.findAll()) {
            dataMapperService.addItemToItemDTOList(itemDTOs, item);
        }
        return itemDTOs;
    }

    public List<ItemDTO> findAllItemsBySearchTextAndCategory(String searchText, String category) {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        if (category.equals("all") && searchText.equals("all")) {
            for (Item item : itemRepository.findAll()) {
                dataMapperService.addItemToItemDTOList(itemDTOs, item);
            }
        } else if (category.equals("all")) {
            for (Item item : itemRepository.findAllByTitleOrDescription(searchText)) {
                dataMapperService.addItemToItemDTOList(itemDTOs, item);
            }
        } else if (searchText.equals("all")) {
            for (Item item : itemRepository.findAll()) {
                if (item.getItemCategory().getCategory().equals(category)) {
                    dataMapperService.addItemToItemDTOList(itemDTOs, item);
                }
            }
        } else {
            for (Item item : itemRepository.findAllByTitleOrDescription(searchText)) {
                if (item.getItemCategory().getCategory().equals(category)) {
                    dataMapperService.addItemToItemDTOList(itemDTOs, item);
                }
            }
        }

        return itemDTOs;
    }

    public ItemSize findItemSizeById(Integer id) {
        return itemSizeRepository.findById(id).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_054, id));
    }

    public ItemSize findItemSizeBySize(String itemSizeSize) {
        return itemSizeRepository.findBySize(itemSizeSize)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_104, itemSizeSize));
    }

    public List<ItemSize> findAllItemSizes() {
        return IterableUtils.toList(itemSizeRepository.findAll());
    }

    public List<ItemDTO> findAllItemsByPaymentTransactionId(Integer paymentTransactionId) {
        if (paymentTransactionId == null) {
            throw new DataException(ErrorCode.ERR_118);
        }
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(paymentTransactionId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, paymentTransactionId));
        Order order = paymentTransaction.getOrder();
        List<Item> orderItems = order.getOrdersDetails().stream().map(OrderDetails::getItem).toList();
        List<ItemDTO> itemDTOs = new ArrayList<>();
        orderItems.forEach(orderItem -> {
            ItemDTO itemDTO = ItemDTO.builder()
                    .itemId(orderItem.getId())
                    .title(orderItem.getTitle())
                    .price(orderItem.getPrice())
                    .build();
            itemDTOs.add(itemDTO);
        });
        return itemDTOs;
    }

    public List<ItemCategoryDTO> findAllItemCategories() {
        List<ItemCategoryDTO> itemCategoryDTOs = new ArrayList<>();
        itemCategoryRepository.findAll().forEach(itemCategory -> {
            ItemCategoryDTO itemCategoryDTO = ItemCategoryDTO.builder()
                    .itemCategoryId(itemCategory.getId())
                    .category(itemCategory.getCategory())
                    .build();
            itemCategoryDTOs.add(itemCategoryDTO);
        });
        return itemCategoryDTOs;
    }

    public List<ItemSetDTO> findAllItemSets() {
        List<ItemSetDTO> itemSetDTOs = new ArrayList<>();
        itemSetRepository.findAll().forEach(itemSet -> {
            ItemSetDTO itemSetDTO = ItemSetDTO.builder()
                    .itemSetId(itemSet.getId())
                    .set(itemSet.getSet())
                    .build();
            itemSetDTOs.add(itemSetDTO);
        });
        return itemSetDTOs;
    }

    public ItemDTO findItemDTOById(Integer itemId) {
        if (itemId == null) {
            throw new DataException(ErrorCode.ERR_047);
        }
        Item item = findItemById(itemId);
        Set<ItemImage> itemImages = item.getItemImages();
        List<ItemImageDTO> itemImageDTOs = new ArrayList<>();
        itemImages.forEach(itemImage -> {
            ItemImageDTO itemImageDTO = dataMapperService.itemImageToItemImageDTO(itemImage);
            itemImageDTOs.add(itemImageDTO);
        });
        return dataMapperService.itemToItemDTO(item, itemImageDTOs);
    }

    public Integer insertItemByItemDTO(ItemDTO itemDTO) {
        Item item = new Item();
        Integer itemId = itemDTO.getItemId();
        if (itemId != null && itemId != 0) {
            item = findItemById(itemDTO.getItemId());
        }

        String itemCategoryCategory = itemDTO.getItemCategory();
        ItemCategory itemCategory = itemCategoryRepository.findByCategory(itemCategoryCategory)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_112, itemCategoryCategory));

        String itemSetSet = itemDTO.getItemSet();
        ItemSet itemSet = itemSetRepository.findBySet(itemSetSet)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_113, itemSetSet));

        item = dataMapperService.itemDTOToItem(itemDTO, item, itemCategory, itemSet);
        itemRepository.save(item);

        Set<ItemImage> itemImages = item.getItemImages();
        if (itemImages != null) {
            itemImageRepository.deleteAll(item.getItemImages());
        }

        Item finalItem = item;
        itemDTO.getItemImages().forEach(itemImageDTO -> {
            ItemImage itemImage = new ItemImage();
            itemImage.setItem(finalItem);
            itemImage.setImageBase64(itemImageDTO.getImageBase64());
            itemImageRepository.save(itemImage);
        });
        return item.getId();
    }


}

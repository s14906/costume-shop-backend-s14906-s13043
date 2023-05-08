package com.costumeshop.controller;

import com.costumeshop.core.sql.entity.Item;
import com.costumeshop.core.sql.entity.ItemColor;
import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.core.sql.repository.ItemColorRepository;
import com.costumeshop.core.sql.repository.ItemRepository;
import com.costumeshop.core.sql.repository.ItemSizeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@CrossOrigin()
public class ItemController {

    private final ItemRepository itemRepository;

    private final ItemSizeRepository itemSizeRepository;

    private final ItemColorRepository itemColorRepository;

    @GetMapping(path = "/items")
    public List<Item> getAllItems() {
        return IterableUtils.toList(itemRepository.findAll());
    }

    @GetMapping(path = "/items/sizes")
    public List<ItemSize> getAllItemSizes() {
        return IterableUtils.toList(itemSizeRepository.findAll());
    }

    @GetMapping(path = "/items/colors")
    public List<ItemColor> getAllItemColors() {
        return IterableUtils.toList(itemColorRepository.findAll());
    }

}

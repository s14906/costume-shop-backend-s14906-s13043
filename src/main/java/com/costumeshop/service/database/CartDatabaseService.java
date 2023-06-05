package com.costumeshop.service.database;

import com.costumeshop.core.sql.entity.Item;
import com.costumeshop.core.sql.entity.ItemCart;
import com.costumeshop.core.sql.entity.ItemSize;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.ItemCartRepository;
import com.costumeshop.exception.DataException;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.codes.InfoCode;
import com.costumeshop.info.utils.CodeMessageUtils;
import com.costumeshop.model.dto.AddToCartDTO;
import com.costumeshop.model.dto.CartItemDTO;
import com.costumeshop.service.DataMapperService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartDatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(CartDatabaseService.class);
    private final DataMapperService dataMapperService;
    private final ItemDatabaseService itemDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final ItemCartRepository itemCartRepository;

    public List<CartItemDTO> findCartItemsByUserId(Integer userId) {
        if (userId == null) {
            throw new DataException(ErrorCode.ERR_017);
        }
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (ItemCart itemCart : itemCartRepository.findAllByUserId(userId)) {
            CartItemDTO cartItemDTO = dataMapperService.cartItemToCartItemDTO(itemCart, userId);
            cartItemDTOs.add(cartItemDTO);
        }
        return cartItemDTOs;
    }

    public void insertCartItemByAddToCartDTO(AddToCartDTO addToCartDTO) {
        try {
            User user = userDatabaseService.findUserById(addToCartDTO.getUserId());
            Item item = itemDatabaseService.findItemById(addToCartDTO.getItemId());

            ItemSize itemSize = itemDatabaseService.findItemSizeById(addToCartDTO.getItemSizeId());

            List<ItemCart> existingCartItems =
                    itemCartRepository.findAllByUserAndItemAndItemSize(user, item, itemSize);

            ItemCart itemCart = new ItemCart();
            itemCart.setUser(user);
            itemCart.setItem(item);
            itemCart.setItemSize(itemSize);

            if (!existingCartItems.isEmpty()) {
                existingCartItems.forEach(existingCartItem -> {
                    existingCartItem.setItemAmount(existingCartItem.getItemAmount() + addToCartDTO.getItemAmount());
                    itemCartRepository.save(existingCartItem);
                    CodeMessageUtils.logMessage(InfoCode.INFO_022, user.getId(), existingCartItem.getId(), logger);
                });
            } else {
                itemCart.setItemAmount(addToCartDTO.getItemAmount());
                itemCartRepository.save(itemCart);
                CodeMessageUtils.logMessage(InfoCode.INFO_023, user.getId(), itemCart.getId(), logger);
            }
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    public void deleteCartItemByCartItemId(Integer itemCartId) {
        ItemCart itemCart = itemCartRepository.findById(itemCartId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_095, itemCartId));
        itemCartRepository.delete(itemCart);
    }

    public void deleteCartItemsByUserId(Integer userId) {
        if (userId == null) {
            throw new DataException(ErrorCode.ERR_017);
        }
        List<ItemCart> itemCarts = itemCartRepository.findAllByUserId(userId);
        itemCartRepository.deleteAll(itemCarts);
    }
}

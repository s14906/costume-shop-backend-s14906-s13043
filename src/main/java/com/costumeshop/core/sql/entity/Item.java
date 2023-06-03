package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer categoryId;

    private Integer itemSetId;

    private String description;

    private BigDecimal price;

    private String title;

    private Integer quantity;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "item_id")
    private Set<ItemImage> itemImages;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "item_id")
    private Set<ItemCart> itemCarts;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "item_id")
    private Set<OrderDetails> ordersDetails;

}

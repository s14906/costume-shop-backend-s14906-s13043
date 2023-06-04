package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private String description;

    private BigDecimal price;

    private String title;

    private Integer quantity;

    private Integer visible;

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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ItemCategory itemCategory;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "item_set_id")
    private ItemSet itemSet;

}

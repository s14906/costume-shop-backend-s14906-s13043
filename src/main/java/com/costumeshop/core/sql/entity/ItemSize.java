package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "item_size")
public class ItemSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String size;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "id")
    private Set<ItemCart> itemCarts;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "item_size_id")
    private Set<OrderDetails> ordersDetails;
}

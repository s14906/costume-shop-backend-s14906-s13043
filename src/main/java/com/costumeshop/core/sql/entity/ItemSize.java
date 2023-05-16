package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "item_size")
public class ItemSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String size;

    @JsonBackReference
    @OneToMany
    @JoinColumn(name = "id")
    private List<ItemCart> itemCarts;
}

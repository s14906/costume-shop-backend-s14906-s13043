package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idCategory;

    private Integer idItemSet;

    private String description;
    private Double price;
    private String title;

    private Integer quantity;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "id")
    private List<ItemImage> itemImages;

}

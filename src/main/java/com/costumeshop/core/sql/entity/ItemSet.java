package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "item_set")
public class ItemSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String set;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "id")
    private Set<Item> items;
}

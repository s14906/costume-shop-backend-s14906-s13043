package com.costumeshop.core.sql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String role;
    private String password;
    private String username;
    private Integer emailVerified;
    private String name;
    private String secondName;
    private String surname;
    private Integer userRoleId;
}

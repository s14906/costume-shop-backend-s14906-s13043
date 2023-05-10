package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

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

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_user_role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<UserRole> userRoles;

    private String phone;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "id")
    private List<Address> addresses;
}

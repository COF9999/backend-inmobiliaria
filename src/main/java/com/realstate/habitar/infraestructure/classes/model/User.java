package com.realstate.habitar.infraestructure.classes.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    private String identification;

    @Version
    private Long version;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "hub_id")
    private String hubId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"user_id", "role_id"})
            }
    )
    private Set<Role> roles = new HashSet<>();

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isAdmin;

    @Column(name = "is_active")
    private Boolean isActive;


    public void replaceRol(Role oldRol,Role newRole){
        if (this.getRoles().remove(oldRol)){
            this.getRoles().add(newRole);
        }else {
            throw new IllegalArgumentException("El usuario no tiene el rol viejo");
        }
    }
}

/*
@Builder
@Data
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String identification;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @JsonIgnoreProperties({"user",
            "handler",
            "hibernateLazyInitializer"
    })
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private List<Role> roles = new ArrayList<>();

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isAdmin;

    @Column(name = "is_active")
    private Boolean isActive;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", isAdmin=" + isAdmin +
                '}';
    }
}

 */
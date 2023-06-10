package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permissions;

    public Role(String name,List<Permission> permissions) {
        this.name=name;
        this.permissions=permissions;
    }

    public Role(Integer id,String  name) {
        this.id=id;
        this.name=name;
    }

    public static Role toRole(Role tariff) {
        return Role
                .builder()
                .name(tariff.getName())
                .permissions(tariff.getPermissions())
                .build();
    }
}

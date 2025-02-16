package com.roomster.roomsterbackend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "houses")
public class HouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    @Column(name = "house_name")
    private String houseName;

    @Column(name = "warn_id")
    private Long warnId;

    @ManyToOne()
    @JoinColumn(name = "warn_id", insertable = false, updatable = false)
    @JsonBackReference
    private WardEntity ward;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserEntity user;

    @OneToMany(mappedBy = "house" ,cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<InforRoomEntity> rooms;

    // Constructors, getters, and setters
}

package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tenant")
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Long id;

    @Column(name = "tenant_name")
    private String name;

    @Column(name = "tenant_age")
    private int age;

    @Column(name = "tenant_gender")
    private String gender;

    @Column(name = "tenant_phone_number")
    private String phoneNumber;

    @Column(name = "tenant_identifier")
    private String identifier;

    @Column(name = "tenant_email")
    private String email;

    @Column(name = "room_id")
    private Long idRoom;

    @ManyToOne()
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonBackReference
    InforRoomEntity room;
}

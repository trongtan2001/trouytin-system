package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "wards")
public class WardEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wardId;

    @Column(name = "ward_name")
    private String wardName;

    @Column(name = "district_id")
    private Long districtId;

    @ManyToOne()
    @JoinColumn(name = "district_id", insertable = false, updatable = false)
    @JsonBackReference
    private DistrictEntity district;


}
package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
 * Entity to description destination payment like (Vnpay, Momopay, ZaloPay...)
 * */
@Entity
@Table(name = "payment_destination")
@Getter
@Setter
public class PaymentDestinationEntity extends BaseSecurityEntity {

    @Column(name = "des_logo", length = 250)
    private String desLogo;

    @Column(name = "desShort_name", length = 50)
    private String desShortName;

    @Column(name = "des_name", length = 250)
    private String desName;

    @Column(name = "des_sort_index")
    private Integer desSortIndex;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "paymentDestinations")
    @JsonManagedReference
    private List<PaymentEntity> payments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id", updatable = false)
    private PaymentDestinationEntity parent;

}

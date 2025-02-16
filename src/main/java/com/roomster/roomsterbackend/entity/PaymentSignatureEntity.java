package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity to store signature of merchant and payment transaction person
 * **/
@Entity
@Table(name = "payment_signature")
@Getter
@Setter
public class PaymentSignatureEntity extends BaseSecurityEntity {

    @Column(name = "sign_value", length = 50)
    private String signValue;

    @Column(name = "sign_algo", length = 50)
    private String signAlgo;

    @Column(name = "sign_own", length = 50)
    private String signOwn;

    @Column(name = "is_valid")
    private Boolean isValid;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "id", updatable = false)
    @JsonBackReference
    private PaymentEntity paymentSignature;
}

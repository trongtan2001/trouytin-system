package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter
@Setter
public class PaymentEntity extends BaseSecurityEntity {

    @Column(name = "payment_content", length = 250)
    private String paymentContent;

    @Column(name = "payment_currency", length = 10)
    private String paymentCurrency;

    @Column(name = "payment_ref_id", length = 50)
    private String paymentRefId;

    @Column(name = "required_amount")
    private BigDecimal requiredAmount;

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column(name = "expire_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    @Column(name = "payment_language", length = 10)
    private String paymentLanguage;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "payment_last_message", length = 250)
    private String paymentLastMessage;

    @ManyToOne
    @JoinColumn(name = "merchant_id" , referencedColumnName = "id", updatable = false)
    @JsonBackReference
    private MerchantEntity merchant;

    @ManyToOne
    @JoinColumn(name = "payment_destination_id" , referencedColumnName = "id", updatable = false)
    @JsonBackReference
    private PaymentDestinationEntity paymentDestinations;

    @OneToMany(mappedBy = "paymentTransaction")
    @JsonManagedReference
    private List<PaymentTransactionEntity> paymentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "paymentSignature")
    @JsonManagedReference
    private List<PaymentSignatureEntity> paymentSignatures = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    @JsonBackReference
    private UserEntity userPayment;
}

package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.Date;

/*
 * Entity to store result payment transaction
 * */
@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
public class PaymentTransactionEntity extends BaseSecurityEntity {

    @Column(name = "tran_message", length = 250)
    private String tranMessage;

    @Column(name = "tran_payload", columnDefinition = "TEXT")
    @Lob
    private String tranPayload;

    @Column(name = "tran_status", length = 10)
    private String tranStatus;

    @Column(name = "tran_amount")
    private BigDecimal tranAmount;

    @Column(name = "tran_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tranDate;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "id", updatable = false)
    @JsonBackReference
    private PaymentEntity paymentTransaction;
}

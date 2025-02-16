package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class TransactionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "extension_days")
    private Integer extensionDays;

    @Column(name = "expired")
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    @JsonBackReference
    private UserEntity userTransaction;

    @ManyToOne
    @JoinColumn(name = "package_id", referencedColumnName = "id")
    @JsonBackReference
    private ServicePackageEntity servicePackage;
}

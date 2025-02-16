package com.roomster.roomsterbackend.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonBackReference
    private InforRoomEntity room;

    @Column(name = "electricity")
    private BigDecimal electricity;

    @Column(name = "water")
    private BigDecimal water;

    @Column(name = "service")
    private BigDecimal service;
    
    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "total_payment")
    private BigDecimal totalPayment = BigDecimal.ZERO;
    
    @Column(name = "status_payment")
    private String statusPayment = "N";

    @Column(name = "payment_date")
    private Date paymentDate;

}

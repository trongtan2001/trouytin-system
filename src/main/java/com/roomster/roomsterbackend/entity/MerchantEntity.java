package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity to store merchant in payment
 * **/
@Entity
@Table(name = "merchant")
@Getter
@Setter
public class MerchantEntity extends BaseSecurityEntity{
    @Column(name = "merchant_name", length = 250)
    private String merchantName;

    @Column(name = "merchant_web_link", length = 250)
    private String merchantWebLink;

    @Column(name = "merchant_ipn_url", length = 250)
    private String merchantIpnUrl;

    @Column(name = "merchant_return_url", length = 250)
    private String merchantReturnUrl;

    @Column(name = "secret_key", length = 50)
    private String secretKey;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "merchant")
    @JsonManagedReference
    private List<PaymentEntity> payments = new ArrayList<>();
}

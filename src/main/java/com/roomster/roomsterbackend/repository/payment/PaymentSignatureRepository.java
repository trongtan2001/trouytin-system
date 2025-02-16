package com.roomster.roomsterbackend.repository.payment;

import com.roomster.roomsterbackend.entity.PaymentSignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentSignatureRepository extends JpaRepository<PaymentSignatureEntity, String> {
}

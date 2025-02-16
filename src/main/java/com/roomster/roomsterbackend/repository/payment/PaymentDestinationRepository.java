package com.roomster.roomsterbackend.repository.payment;

import com.roomster.roomsterbackend.entity.PaymentDestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDestinationRepository extends JpaRepository<PaymentDestinationEntity, String> {
}

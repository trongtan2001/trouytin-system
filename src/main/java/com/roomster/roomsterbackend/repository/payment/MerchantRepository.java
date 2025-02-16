package com.roomster.roomsterbackend.repository.payment;

import com.roomster.roomsterbackend.entity.MerchantEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, String> {
}

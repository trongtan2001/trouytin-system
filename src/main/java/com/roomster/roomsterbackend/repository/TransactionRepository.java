package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.TransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByUserTransaction_IdAndExpiredFalse(Long userId);
    List<TransactionEntity> findByUserTransactionIdOrderByPurchaseDateDesc(Long userId, Pageable pageable);

    List<TransactionEntity> findAllByOrderByPurchaseDateDesc(Pageable pageable);

    Long countByUserTransaction_Id(Long userId);
}

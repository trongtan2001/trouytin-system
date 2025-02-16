package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.BankMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BankMethodRepository extends JpaRepository<BankMethodEntity, Long> {
    List<BankMethodEntity> findAllByUserId(Long userId);
}

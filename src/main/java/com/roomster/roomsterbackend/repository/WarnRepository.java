package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.WardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarnRepository extends JpaRepository<WardEntity, Long> {

    @Query("SELECT w FROM WardEntity w WHERE w.districtId = ?1")
    public List<WardEntity> getAllWarnByDistrictId(Long districtId);
}

package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, Long> {
    @Query("SELECT d FROM DistrictEntity d WHERE d.cityId = ?1")
    public List<DistrictEntity> getAllDistrictByCityId(Long cityId);

}

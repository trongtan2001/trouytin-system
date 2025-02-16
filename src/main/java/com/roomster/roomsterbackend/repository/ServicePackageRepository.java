package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.ServicePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackageEntity, Long> {
    Optional<ServicePackageEntity> findByName(String name);
}

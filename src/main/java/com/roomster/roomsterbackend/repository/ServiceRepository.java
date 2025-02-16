package com.roomster.roomsterbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.roomster.roomsterbackend.entity.RoomServiceEntity;
import com.roomster.roomsterbackend.entity.ServiceHouseEntity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceHouseEntity, Long> {

	List<ServiceHouseEntity> findAll();

	List<ServiceHouseEntity> findAllByUserId(Long userId);

	Optional<ServiceHouseEntity> findById(Long id);

	void save(RoomServiceEntity roomServiceUpdate);

	@Query("SELECT COUNT(s) FROM ServiceHouseEntity s " +
			"WHERE s.serviceName = :serviceName " +
			"AND (:serviceId IS NULL OR s.serviceId != :serviceId) " +
			"AND (:userId IS NULL OR s.user.id = :userId)")
	Long countByNameAndDifferentId(@Param("serviceName") String serviceName, @Param("serviceId") Long serviceId, @Param("userId") Long id);
}

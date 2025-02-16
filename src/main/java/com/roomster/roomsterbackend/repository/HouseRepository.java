package com.roomster.roomsterbackend.repository;

import java.util.List;
import java.util.Optional;

import com.roomster.roomsterbackend.entity.HouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface HouseRepository extends JpaRepository<HouseEntity, Long> {

	List<HouseEntity> findAll();

	List<HouseEntity> findAllByUserId(Long id);

	Optional<HouseEntity> findById(Long id);

	@Query("SELECT COUNT(h) FROM HouseEntity h " +
			"JOIN h.ward w " +
			"WHERE (w.wardId = :wardId AND h.address = :currentAddress) " +
			"AND (:houseId IS NULL OR h.houseId != :houseId)")
	Long countHousesDifferentAddress(@Param("currentAddress") String currentAddress,
									 @Param("wardId") Long wardId,
									 @Param("houseId") Long houseId);

	@Query("SELECT COUNT(h) FROM HouseEntity h " +
			"JOIN h.ward w " +
			"WHERE (w.wardId = :wardId AND h.houseName = :houseName) " +
			"AND (:houseId IS NULL OR h.houseId != :houseId)")
	Long countHousesDifferentName(@Param("houseName") String houseName,
								  @Param("wardId") Long wardId,
								  @Param("houseId") Long houseId);

}

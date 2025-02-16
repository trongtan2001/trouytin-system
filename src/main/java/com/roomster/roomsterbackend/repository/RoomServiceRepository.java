package com.roomster.roomsterbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.roomster.roomsterbackend.entity.RoomServiceEntity;

@Repository
public interface RoomServiceRepository extends JpaRepository<RoomServiceEntity, Long> {
    @Modifying
    @Query(value = "DELETE FROM room_services re WHERE re.room_services_id = :id", nativeQuery = true)
    void deleteRoomService(@Param("id") Long id);
}

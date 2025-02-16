package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.InforRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InforRoomRepository extends JpaRepository<InforRoomEntity, Long> {
}

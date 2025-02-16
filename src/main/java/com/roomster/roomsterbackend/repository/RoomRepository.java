package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.InforRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<InforRoomEntity, Long> {
    @Override
    List<InforRoomEntity> findAll();

    @Query("SELECT COUNT(r) FROM InforRoomEntity r " +
            "WHERE r.emptyRoom = 1 " +
            "AND r.house.user.id = :userId")
    Long countEmptyRooms(@Param("userId") Long userId);

    @Query("SELECT r FROM InforRoomEntity r JOIN r.orders o " +
            "WHERE r.house.user.id=:userId " +
            "AND (o.statusPayment = 'N' OR o.statusPayment = 'P')")
    List<InforRoomEntity> findRoomsByPaymentStatusNotPaid(@Param("userId") Long userId);

    @Query("SELECT COUNT(ir) FROM InforRoomEntity ir " +
            "WHERE ir.houseId = :houseId " +
            "AND ir.numberRoom = :numberRoom " +
            "AND (:roomId IS NULL OR ir.id != :roomId)")
    Long countRoomsDifferentStt(@Param("numberRoom") int stt,
                                     @Param("roomId") Long roomId,
                                     @Param("houseId") Long houseId);
}

package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "room_services")
public class RoomServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_services_id")
    Long RoomService;

    @Column(name = "room_id")
    Long roomId;

    @Column(name = "service_id")
    Long serviceId;

    @ManyToOne
    @JoinColumn(name = "room_id", insertable=false, updatable=false)
    @JsonBackReference
    private InforRoomEntity inforRoomEntity;

    @ManyToOne
    @JoinColumn(name = "service_id", insertable=false, updatable=false)
    @JsonManagedReference
    private ServiceHouseEntity serviceHouse;

}

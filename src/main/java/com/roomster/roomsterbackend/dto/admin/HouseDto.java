package com.roomster.roomsterbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roomster.roomsterbackend.entity.InforRoomEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HouseDto {
    private Long houseId;
    private String houseName;
    private Long warnId;
    private Long districtId;
    private Long cityId;
    private String address;
    private List<InforRoomEntity> rooms;
}

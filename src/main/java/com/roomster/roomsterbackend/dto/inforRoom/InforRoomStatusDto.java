package com.roomster.roomsterbackend.dto.inforRoom;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InforRoomStatusDto {
    private String houseName;
    private List<Integer> roomName;

}

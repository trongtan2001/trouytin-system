package com.roomster.roomsterbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartUser {
    private Long userId;
    private String userName;
    private String phoneNumber;
    private String images;
}

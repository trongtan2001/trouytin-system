package com.roomster.roomsterbackend.dto.post;

import com.roomster.roomsterbackend.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDtoImp{
    private Long id;

    private String title;

    private String address;

    private String description;

    private String object;

    private String[] convenient;

    private String surroundings;

    private String postType;

    private UserDto createdBy;

    private Date createdDate;

    private String rotation;

    private String acreage;

    private BigDecimal electricityPrice;

    private BigDecimal price;

    private BigDecimal waterPrice;

    private int stayMax;

    private int emptyRoom;

    private int numberRoom;

    private Long inforRoomId;
}

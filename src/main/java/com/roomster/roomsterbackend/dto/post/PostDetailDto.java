package com.roomster.roomsterbackend.dto.post;

import java.math.BigDecimal;
import java.util.Date;

public interface PostDetailDto {
    Long getId();

    String getTitle();

    String getAddress();

    String getDescription();

    String getObject();

    String getConvenient();

    String getSurroundings();

    Long getPostType();

    Long getCreatedBy();

    Date getCreatedDate();

    String getRotation();

    String getAcreage();

    BigDecimal getElectricityPrice();

    BigDecimal getPrice();

    BigDecimal getWaterPrice();

    int getStayMax();

    int getEmptyRoom();

    int getNumberRoom();

    Long getInforRoomId();
}

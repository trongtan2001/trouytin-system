package com.roomster.roomsterbackend.dto.post;

import java.math.BigDecimal;
import java.util.Date;

public interface PostDtoWithRating {
    Long getId();

    String getTitle();

    String getAddress();

    Date getCreatedDate();

    BigDecimal getPrice();

    Boolean getIsDeleted();

    String getImage();

    Double getAverageRating();
}

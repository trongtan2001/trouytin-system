package com.roomster.roomsterbackend.dto.post;

import java.math.BigDecimal;
import java.util.Date;

public interface PostByWishList {
     Long getId();
     String getTitle();
     String getAddress();
     Date getCreatedDate();
     BigDecimal getPrice();
     String getImage();
     Long getWishListItemId();
}

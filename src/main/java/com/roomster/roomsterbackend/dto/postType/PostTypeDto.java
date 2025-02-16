package com.roomster.roomsterbackend.dto.postType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTypeDto {

    private String name;

    private String code;

    private boolean isDeleted;

    private Date createdDate;

    private Date modifiedDate;

    private Long createdBy;

    private Long modifiedBy;

}

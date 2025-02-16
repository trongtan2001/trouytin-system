package com.roomster.roomsterbackend.dto.post;

import lombok.Data;

@Data
public class ProvinceDtoWithImage{
    private String provinceName;
    private Integer totalPosts;
    private String image;
}

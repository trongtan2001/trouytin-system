package com.roomster.roomsterbackend.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostDetailDtoWithImage {
    private List<PostImageDto> images;
    private PostDetailDtoImp postDetail;
}

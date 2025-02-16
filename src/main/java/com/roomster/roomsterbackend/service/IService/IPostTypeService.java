package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.postType.PostTypeDto;

import java.util.List;


public interface IPostTypeService {
    List<PostTypeDto> getAllPostType();

    BaseResponse addPostType(PostTypeDto postTypeDto);
}

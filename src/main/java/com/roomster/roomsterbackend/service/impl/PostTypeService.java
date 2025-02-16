package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.postType.PostTypeDto;
import com.roomster.roomsterbackend.mapper.PostTypeMapper;
import com.roomster.roomsterbackend.repository.PostTypeRepository;
import com.roomster.roomsterbackend.service.IService.IPostTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostTypeService implements IPostTypeService {
    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private PostTypeMapper postTypeMapper;
    @Override
    public List<PostTypeDto> getAllPostType() {
        return postTypeRepository.findAll().stream()
                .map(dto -> postTypeMapper.entityToDto(dto))
                .collect(Collectors.toList());
    }

    @Override
    public BaseResponse addPostType(PostTypeDto postTypeDto) {
        try {
            postTypeDto.setDeleted(false);
            postTypeRepository.save(postTypeMapper.dtoToEntity(postTypeDto));
        }catch (Exception ex){
            BaseResponse.error(ex.getMessage());
        }
        return BaseResponse.success("Insert post type successfully");
    }
}

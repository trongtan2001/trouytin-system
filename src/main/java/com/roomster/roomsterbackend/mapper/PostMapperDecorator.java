package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.post.PostDto;
import com.roomster.roomsterbackend.entity.PostEntity;
import com.roomster.roomsterbackend.repository.PostTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class PostMapperDecorator implements PostMapper {
    @Autowired
    @Qualifier("delegate")
    private PostMapper delegate;

    @Autowired
    private PostTypeRepository repository;

    @Override
    public PostDto entityToDto(PostEntity postEntity) {
        PostDto postDTO = delegate.entityToDto(postEntity);
        postDTO.setPost_type(postEntity.getPostType().getName());
        return postDTO;
    }

    @Override
    public PostEntity dtoToEntity(PostDto postDTO) {
        PostEntity postEntity = delegate.dtoToEntity(postDTO);
        postEntity.setPostType(repository.getPostEntityByName(postDTO.getPost_type()));
        return postEntity;
    }
}

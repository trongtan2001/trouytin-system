package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.rating.RatingDto;
import com.roomster.roomsterbackend.entity.RatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    @Mapping(target = "ratingId", source = "id")
    RatingDto entityToDTO(RatingEntity ratingEntity);

    @Mapping(target = "id", source = "ratingId")
    RatingEntity dtoToEntity(RatingDto likePostDTO);
    RatingEntity updateRating(@MappingTarget RatingEntity oldRating, RatingEntity newRating);

}

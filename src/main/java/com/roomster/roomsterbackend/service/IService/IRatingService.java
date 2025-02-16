package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.rating.AverageRatingPoint;
import com.roomster.roomsterbackend.dto.rating.RatingDto;

import java.security.Principal;
import java.util.List;

public interface IRatingService {
    RatingDto saveNewRating(RatingDto ratingDto, Principal connectedUser);

    BaseResponse updateRating(Long ratingId, RatingDto ratingDto);

    List<RatingDto> getAllRatingByPost(Long postId);

    AverageRatingPoint getGroupRatingByPost(Long postId);

    void deleteRating(Long ratingId);
}

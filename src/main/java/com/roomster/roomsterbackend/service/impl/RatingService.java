package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.rating.AverageRatingPoint;
import com.roomster.roomsterbackend.dto.rating.RatingDto;
import com.roomster.roomsterbackend.dto.rating.RatingWithGroup;
import com.roomster.roomsterbackend.entity.RatingEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.mapper.RatingMapper;
import com.roomster.roomsterbackend.repository.RatingRepository;
import com.roomster.roomsterbackend.service.IService.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService implements IRatingService {
    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    RatingMapper ratingMapper;
    @Override
    public RatingDto saveNewRating(RatingDto ratingDto, Principal connectedUser) {
        var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();
        List<RatingEntity> ratings = ratingRepository.getRatingEntitiesByPostId(ratingDto.getPostId());
        for (RatingEntity item: ratings
             ) {
            if(item.getUserId().equals(user.getId())){
                return null;
            }
        }
        ratingDto.setUserId(user.getId());
        return ratingMapper.entityToDTO(ratingRepository.save(ratingMapper.dtoToEntity(ratingDto)));
    }

    @Override
    public BaseResponse updateRating(Long ratingId, RatingDto ratingDto) {
        Optional<RatingEntity> oldRating = ratingRepository.findById(ratingId);
        if(oldRating.isPresent()){
            oldRating.get().setStarPoint(ratingDto.getStarPoint());
            ratingRepository.save(oldRating.get());
            return BaseResponse.success("Cập Nhật Thành Công!");
        }
        return BaseResponse.error("Cập Nhật Thất Bại!");
    }

    @Override
    public List<RatingDto> getAllRatingByPost(Long postId) {
        return ratingRepository.getRatingEntitiesByPostId(postId)
                .stream()
                .map(likePostEntity -> ratingMapper.entityToDTO(likePostEntity))
                .collect(Collectors.toList());
    }

    @Override
    public AverageRatingPoint getGroupRatingByPost(Long postId) {
        AverageRatingPoint averageRatingPoint = new AverageRatingPoint();
        List<RatingWithGroup> ratingWithGroups = ratingRepository.getGroupRatingByPostId(postId);
        convertRatingToAverageRating(averageRatingPoint, ratingWithGroups);

        return averageRatingPoint;
    }

    @Override
    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }

    private void convertRatingToAverageRating(AverageRatingPoint averageRatingPoint, List<RatingWithGroup> ratingWithGroups){
        double totalRating = 0;
        double totalCount = 0;
        for (RatingWithGroup item: ratingWithGroups
             ) {
            totalRating += item.getStarPoint() * item.getCount();
            totalCount += item.getCount();
        }
        double averageRating = ratingWithGroups.isEmpty() ? 0 : totalRating / totalCount;

        averageRatingPoint.setAverageStarPoint(averageRating);
        averageRatingPoint.setDetail(ratingWithGroups);
    }
}

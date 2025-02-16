package com.roomster.roomsterbackend.dto.rating;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingDto {
    private Long ratingId;
    private Long postId;
    private Long userId;
    private double starPoint;
}

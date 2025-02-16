package com.roomster.roomsterbackend.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageRatingPoint {
    private double averageStarPoint;
    private List<RatingWithGroup> detail;
}

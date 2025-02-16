package com.roomster.roomsterbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusPost {
    private Long percentApproved;
    private Long percentRejected;
    private Long percentReview;
}

package com.roomster.roomsterbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ratings", uniqueConstraints = { @UniqueConstraint(name = "UP_ratings", columnNames = { "post_id", "user_id" }) })
public class RatingEntity extends BaseEntity {

    @Column(name = "star_point")
    private double starPoint;
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "user_id")
    private Long userId;

}

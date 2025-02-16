package com.roomster.roomsterbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentEnity extends BaseEntity {

    @Column(name = "parent_comment")
    private Long parentComment;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private boolean status;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;

}

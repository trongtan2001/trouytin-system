package com.roomster.roomsterbackend.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roomster.roomsterbackend.dto.user.PartUser;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentPostDto {
    private Long commentPostId;
    private Long parentComment;
    private Long userId;
    private PartUser partUser;
    private Long postId;
    private String content;
    private boolean status;
    private Date createdDate;
    private Date modifiedDate;
}

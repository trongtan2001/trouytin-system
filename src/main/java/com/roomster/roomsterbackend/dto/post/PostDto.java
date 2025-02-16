package com.roomster.roomsterbackend.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.dto.comment.CommentPostDto;
import com.roomster.roomsterbackend.dto.inforRoom.InforRoomDto;
import com.roomster.roomsterbackend.dto.rating.RatingDto;
import com.roomster.roomsterbackend.dto.user.UserDto;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long postId;
    private String title;
    private String address;
    private String description;
    private String object;
    private String convenient;
    private String surroundings;
    private String post_type;
    private boolean isDeleted;
    private Status status;
    private InforRoomDto roomDto;
    @JsonIgnore
    private UserDto authorId;
    private String rotation;
    private Date createdDate;
    private Date modifiedDate;
    private Long modifiedBy;
    private List<String> imageUrlList;
    private List<CommentPostDto> commentPostDTOList;
    private List<RatingDto> ratingDTOList;
}

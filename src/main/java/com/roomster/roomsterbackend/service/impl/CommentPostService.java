package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.comment.CommentPostDto;
import com.roomster.roomsterbackend.dto.user.PartUser;
import com.roomster.roomsterbackend.entity.CommentEnity;
import com.roomster.roomsterbackend.entity.PostEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.mapper.CommentMapper;
import com.roomster.roomsterbackend.repository.CommentPostRepository;
import com.roomster.roomsterbackend.repository.PostRepository;
import com.roomster.roomsterbackend.repository.RoleRepository;
import com.roomster.roomsterbackend.repository.UserRepository;
import com.roomster.roomsterbackend.service.IService.ICommentPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentPostService implements ICommentPostService {
    @Autowired
    CommentPostRepository commentPostRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public CommentPostDto saveNewComment(CommentPostDto commentPostDTO, Principal connectedUser) {
        var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        commentPostDTO.setUserId(user.getId());
        commentPostDTO.setStatus(true);
        return commentMapper.entityToDTO(commentPostRepository.save(commentMapper.dtoToEntity(commentPostDTO)));
    }

    @Override
    public CommentPostDto updateComment(Long commentId, CommentPostDto commentPostDTO, Principal connectedUser) {
        Optional<CommentEnity> oldComment = commentPostRepository.findById(commentId);
        var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();
        if (oldComment.isPresent() && user != null) {
            if(user.getId().equals(oldComment.get().getUserId())) {
                oldComment.get().setContent(commentPostDTO.getContent());
                commentPostRepository.save(oldComment.get());
                return commentMapper.entityToDTO(oldComment.get());
            }
        }
        return null;
    }

    @Override
    public BaseResponse deleteComment(Long commentId, Principal connectedUser) {
        var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();
        Optional<CommentEnity> comment = commentPostRepository.findById(commentId);
        try {
            if(comment.isPresent() && user != null){
                if(comment.get().getUserId().equals(user.getId())){
                    commentPostRepository.deleteById(commentId);
                    return BaseResponse.success("Bạn đã xóa bình luận!");
                }
            }
        } catch (Exception ex) {
            BaseResponse.error(ex.getMessage());
        }
        return BaseResponse.error("Không thể xóa bình luận");
    }

    @Override
    public List<CommentPostDto> getAllCommentOfPost(Long postId) {
        List<CommentPostDto> commentPostDtos = commentPostRepository.getCommentByPostId(postId).stream().map(commentEnity -> commentMapper.entityToDTO(commentEnity)).toList();

        for (CommentPostDto item : commentPostDtos
        ) {
            PartUser partUser = new PartUser();
            UserEntity user = userRepository.findById(item.getUserId()).orElseThrow();
            partUser.setUserId(user.getId());
            partUser.setUserName(user.getUserName());
            partUser.setImages(user.getImages());
            Optional<PostEntity> post = postRepository.findById(item.getPostId());
            if(post.isPresent()) {
                if (item.getUserId().equals(post.get().getCreatedBy())) {
                    partUser.setUserName("Tác Giả");
                }
            }
            item.setPartUser(partUser);
        }
        return commentPostDtos;
    }
}

package com.roomster.roomsterbackend.controller.management;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.comment.CommentPostDto;
import com.roomster.roomsterbackend.service.IService.ICommentPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {
    private final ICommentPostService service;
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE')")
    @PostMapping("/new")
    public BaseResponse saveNewCommentPost(@RequestBody CommentPostDto commentPostDTO, Principal connectedUser) {
        try {
            service.saveNewComment(commentPostDTO, connectedUser);
        }catch (Exception ex){
            BaseResponse.error(ex.getMessage());
        }
        return BaseResponse.success("Thêm Bình Luận Thành Công");
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE','ROLE_ADMIN')")
    @PutMapping("/update")
    public BaseResponse updateCommentPost(@RequestParam Long commentId, @RequestBody CommentPostDto commentPostDTO, Principal connectedUser) {
        try {
           CommentPostDto commentPost = service.updateComment(commentId,commentPostDTO, connectedUser);
           if(commentPost != null){
               return BaseResponse.success("Cập nhật bình luận thành công!");
           }
        }catch (Exception ex){
            return BaseResponse.error("Err: " + ex.getMessage());
        }
        return BaseResponse.error("Không cho phép cập nhật!");
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE','ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public BaseResponse deleteCommentPost(@RequestParam Long commentId, Principal connectedUser) {
        return service.deleteComment(commentId, connectedUser);
    }

    @GetMapping("/list/{postId}")
    public List<CommentPostDto> getAllCommentOfPost(@PathVariable(name = "postId") Long postId) {
        return service.getAllCommentOfPost(postId);
    }
}

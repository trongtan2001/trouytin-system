package com.roomster.roomsterbackend.controller.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.post.PostDto;
import com.roomster.roomsterbackend.service.IService.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@PreAuthorize("hasAnyRole('ROLE_MANAGE','ROLE_ADMIN')")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    private final IPostService service;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE','ROLE_ADMIN')")
    @GetMapping("/approved")
    public List<PostDto> listPostApproved(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getPostsApproved(pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/review")
    public List<PostDto> listPostReview(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getPostsReview(pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/rejected")
    public List<PostDto> listPostRejected(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getPostsRejected(pageable);
    }

    @PostMapping(value = "/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse upsertPost(@RequestPart String postDto, @RequestPart(required = false, name = "images") @Valid List<MultipartFile> images, Principal principal) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PostDto postDTO = objectMapper.readValue(postDto, PostDto.class);
            if (postDTO.getPostId() != null) {
                service.upsertPost(postDTO, images, principal);
                return BaseResponse.success("Cập nhật bài viết thành công!");
            } else {
                service.upsertPost(postDTO, images, principal);
                return BaseResponse.success("Bạn đã thêm bài viết thành công. Trọ Uy Tín xin cảm ơn bạn và đang chờ duyệt.");
            }
        } catch (Exception ex) {
            return BaseResponse.error(ex.getMessage());
        }
    }

    @DeleteMapping(value = "/delete")
    public BaseResponse deletePostById(@RequestParam Long postId) {
        try {
            service.deletePostById(postId);
        } catch (Exception ex) {
            BaseResponse.error("Ex: " + ex.getMessage());
        }
        return BaseResponse.success("Xóa bài viết thành công!");
    }
}

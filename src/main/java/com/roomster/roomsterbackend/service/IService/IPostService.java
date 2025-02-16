package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.dto.post.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface IPostService {

    List<PostDto> getPostsApproved(Pageable pageable);
    List<PostDto> getPostByAuthorId(Pageable pageable, Long authorId);

    PostDto getPostById(Long postId);
    void upsertPost(PostDto postDTO,
                    List<MultipartFile> images, Principal connectedUser) throws IOException;

    List<PostDtoWithRating> getPostByRating(Pageable pageable);

    List<ProvinceDto> getTopOfProvince(Pageable pageable);

    PostDetailDtoImp getPostDetail(Long postId);

    List<PostImageDto> getPostImages(Long postId);

    void deletePostById(Long postId);

    void setIsApprovedPosts(Long[] listPostId);

    List<PostDto> getPostsReview(Pageable pageable);

    List<PostDto> getPostsRejected(Pageable pageable);

    void setIsRejectedPosts(Long[] listPostId);

    ResponseEntity<?> getStatusPost();

    ResponseEntity<?> getTotalPaymentServiceByMonth();

    ResponseEntity<?> sortedPostByPriceDes(Pageable pageable);
    ResponseEntity<?> sortedPostByAcreageDes(Pageable pageable);

    ResponseEntity<?> findPostsAroundLocation(double latitude, double longitude, double radiusInKm);
}
package com.roomster.roomsterbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.base.BaseResultWithDataAndCount;
import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.dto.order.PaymentByMonthDto;
import com.roomster.roomsterbackend.dto.post.*;
import com.roomster.roomsterbackend.entity.PostEntity;
import com.roomster.roomsterbackend.entity.PostTypeEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.mapper.InforRoomMapper;
import com.roomster.roomsterbackend.mapper.PostMapper;
import com.roomster.roomsterbackend.mapper.UserMapper;
import com.roomster.roomsterbackend.repository.PostRepository;
import com.roomster.roomsterbackend.repository.PostTypeRepository;
import com.roomster.roomsterbackend.repository.UserRepository;
import com.roomster.roomsterbackend.service.IService.IPostService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private InforRoomMapper inforRoomMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private UserMapper userMapper;

    private final Cloudinary cloudinary;

    @Override
    public List<PostDto> getPostsApproved(Pageable pageable) {
        List<PostEntity> postPage = postRepository.getAllByStatusAndIsDeleted(pageable, Status.APPROVED, false);
        return postPage.stream()
                .map(postEntity -> postMapper.entityToDto(postEntity))
                .toList();
    }

    @Override
    public List<PostDto> getPostByAuthorId(Pageable pageable, Long authorId) {
        return postRepository.getPostEntityByAuthorId(pageable, authorId)
                .stream()
                .map(postEntity -> postMapper.entityToDto(postEntity))
                .filter(postDto -> !postDto.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long postId) {
        return postMapper.entityToDto(postRepository.findById(postId).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public void upsertPost(PostDto postDTO, List<MultipartFile> images, Principal connectedUser) throws IOException {

        var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        //TODO: Update post
        if (postDTO.getPostId() != null && postDTO.getRoomDto().getInforRoomId() != null) {
            Optional<PostEntity> post = postRepository.findById(postDTO.getPostId());
            if (post.isPresent()) {
                postDTO.setImageUrlList(post.get().getImageUrlList());
                post = Optional.of(postMapper.dtoToEntity(postDTO));
                post.get().setPostType(postTypeRepository.getPostEntityByCode(postDTO.getPost_type()));
                post.get().setDeleted(false);

                // need admin or sp admin accept to APPROVED post
                post.get().setStatus(Status.REVIEW);
                if (postDTO.getRotation() != null) {
                    post.get().setRotation(postDTO.getRotation());
                }

                if (user != null) {
                    post.get().setAuthorId(user);
                }
                if (images != null && !images.isEmpty()) {
                    List<String> imageUrls = new ArrayList<>();
                    for (MultipartFile multipartFile : images) {
                        imageUrls.add(getFileUrls(multipartFile));
                    }
                    post.get().setImageUrlList(imageUrls);
                }
                if (postDTO.getRoomDto() != null) {
                    post.get().setRoomId(inforRoomMapper.dtoToEntity(postDTO.getRoomDto()));
                }
                postRepository.save(post.get());
            }
        } else {
            //TODO: Add post
            PostEntity postEntity = postMapper.dtoToEntity(postDTO);
            postEntity.setPostType(postTypeRepository.getPostEntityByCode(postDTO.getPost_type()));
            postEntity.setDeleted(false);
            // need admin or sp admin accept to APPROVED post
            postEntity.setStatus(Status.REVIEW);
            if (postDTO.getRotation() != null) {
                postEntity.setRotation(postDTO.getRotation());
            }
            if (user != null) {
                postEntity.setAuthorId(user);
            }
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile multipartFile : images) {
                    imageUrls.add(getFileUrls(multipartFile));
                }
                postEntity.setImageUrlList(imageUrls);
            }
            if (postDTO.getRoomDto() != null) {
                postEntity.setRoomId(inforRoomMapper.dtoToEntity(postDTO.getRoomDto()));
            }
            postRepository.save(postEntity);
        }
    }

    @Override
    public List<PostDtoWithRating> getPostByRating(Pageable pageable) {
        return postRepository.getPostByRating(pageable).stream().filter(postDtoWithRating -> !postDtoWithRating.getIsDeleted()).collect(Collectors.toList());
    }

    @Override
    public List<ProvinceDto> getTopOfProvince(Pageable pageable) {
        return postRepository.getTopOfProvince(pageable);
    }

    @Override
    public PostDetailDtoImp getPostDetail(Long postId) {
        Optional<PostDetailDto> postDetailDto = postRepository.getPostDetail(postId);
        PostDetailDtoImp postDetailDtoImp = new PostDetailDtoImp();
        postDetailDto.ifPresent(detailDto -> convertPostDetail(postDetailDtoImp, detailDto));
        return postDetailDtoImp;
    }

    private void convertPostDetail(PostDetailDtoImp postDetailDtoImp, PostDetailDto postDetailDto) {
        postDetailDtoImp.setId(postDetailDto.getId());
        postDetailDtoImp.setTitle(postDetailDto.getTitle());
        postDetailDtoImp.setAddress(postDetailDto.getAddress());
        postDetailDtoImp.setDescription(postDetailDto.getDescription());
        postDetailDtoImp.setObject(postDetailDto.getObject());

        //TODO: Set String convenient to String[]
        String convenient = postDetailDto.getConvenient();
        String[] convenientArray = convenient.split(",");
        postDetailDtoImp.setConvenient(convenientArray);

        postDetailDtoImp.setInforRoomId(postDetailDto.getInforRoomId());

        postDetailDtoImp.setSurroundings(postDetailDto.getSurroundings());

        Optional<PostTypeEntity> postType = postTypeRepository.findById(postDetailDto.getPostType());
        postType.ifPresent(postTypeEntity -> postDetailDtoImp.setPostType(postTypeEntity.getName()));

        Optional<UserEntity> user = userRepository.findById(postDetailDto.getCreatedBy());
        user.ifPresent(userEntity -> postDetailDtoImp.setCreatedBy(userMapper.entityToDto(user.get())));

        postDetailDtoImp.setCreatedDate(postDetailDto.getCreatedDate());
        postDetailDtoImp.setRotation(postDetailDto.getRotation());
        postDetailDtoImp.setAcreage(postDetailDto.getAcreage());
        postDetailDtoImp.setElectricityPrice(postDetailDto.getElectricityPrice());
        postDetailDtoImp.setPrice(postDetailDto.getPrice());
        postDetailDtoImp.setWaterPrice(postDetailDto.getWaterPrice());
        postDetailDtoImp.setStayMax(postDetailDto.getStayMax());
        postDetailDtoImp.setEmptyRoom(postDetailDto.getEmptyRoom());
        postDetailDtoImp.setNumberRoom(postDetailDto.getNumberRoom());
    }

    @Override
    public List<PostImageDto> getPostImages(Long postId) {
        return postRepository.getPostImages(postId);
    }

    @Override
    public void deletePostById(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public void setIsApprovedPosts(Long[] listPostId) {
        for (Long item : listPostId
        ) {
            Optional<PostEntity> post = postRepository.findById(item);
            if (post.isPresent()) {
                post.get().setStatus(Status.APPROVED);
                postRepository.save(post.get());
            }
        }
    }

    @Override
    public List<PostDto> getPostsReview(Pageable pageable) {
        List<PostEntity> postPage = postRepository.getAllByStatusAndIsDeleted(pageable, Status.REVIEW, false);
        // Get the content (posts) from the page
        return postPage.stream()
                .map(postEntity -> postMapper.entityToDto(postEntity))
                .toList();
    }

    @Override
    public List<PostDto> getPostsRejected(Pageable pageable) {
        List<PostEntity> postPage = postRepository.getAllByStatusAndIsDeleted(pageable, Status.REJECTED, false);
        return postPage.stream()
                .map(postEntity -> postMapper.entityToDto(postEntity))
                .toList();
    }

    @Override
    public void setIsRejectedPosts(Long[] listPostId) {
        for (Long item : listPostId
        ) {
            Optional<PostEntity> post = postRepository.findById(item);
            if (post.isPresent()) {
                post.get().setStatus(Status.REJECTED);
                postRepository.save(post.get());
            }
        }
    }

    @Override
    public ResponseEntity<?> getStatusPost() {
        ResponseEntity<?> responseEntity = null;
        try {
            Long countPost = this.postRepository.countByIsDeletedFalse();
            Long countApprovedPost = this.postRepository.countByStatus(Status.APPROVED);
            Long countRejectedPost = this.postRepository.countByStatus(Status.REJECTED);
            Long countReviewPost = this.postRepository.countByStatus(Status.REVIEW);
            StatusPost status;
            if (countPost == 0) {
                status = new StatusPost(0L, 0L, 0L);
                return new ResponseEntity<>(status, HttpStatus.OK);
            }

            Long percentApproved = Math.round((countApprovedPost.doubleValue() / countPost.doubleValue()) * 100.0);
            Long percentRejected = Math.round((countRejectedPost.doubleValue() / countPost.doubleValue()) * 100.0);
            Long percentReview = Math.round((countReviewPost.doubleValue() / countPost.doubleValue()) * 100.0);
            status = new StatusPost(percentApproved, percentRejected, percentReview);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getTotalPaymentServiceByMonth() {
        ResponseEntity<?> responseEntity;
        try {
            List<Object[]> result = postRepository.getTotalPaymentServiceByMonth();
            List<PaymentByMonthDto> paymentByMonthDtoList = result.stream()
                    .map(row -> new PaymentByMonthDto((Integer) row[0], (BigDecimal) row[1]))
                    .toList();
            responseEntity = new ResponseEntity<>(paymentByMonthDtoList, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> sortedPostByPriceDes(Pageable pageable) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<PostDtoWithFilter>> resultToView = new BaseResultWithDataAndCount<>();
        try {
            List<Object[]> resultQuery = postRepository.getAllByOrderPriceDesc(pageable);
            List<PostDtoWithFilter> postDtoWithFilter = resultQuery.stream()
                    .map(row -> new PostDtoWithFilter((Long) row[0], (String) row[1], (String) row[2], (Date) row[3], (Date) row[4], (BigDecimal) row[5], (boolean) row[6], (String) row[7], (String) row[8], (double) row[9]))
                    .collect(Collectors.toList());
            Long countResultQuery = postRepository.countByIsDeletedFalseAndStatus(Status.APPROVED);

            resultToView.set(postDtoWithFilter, countResultQuery);
            response = new ResponseEntity<>(resultToView, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> sortedPostByAcreageDes(Pageable pageable) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<PostDtoWithFilter>> resultToView = new BaseResultWithDataAndCount<>();
        try {
            List<Object[]> resultQuery = postRepository.getAllByOrderAcreageDesc(pageable);
            List<PostDtoWithFilter> postDtoWithFilter = resultQuery.stream()
                    .map(row -> new PostDtoWithFilter((Long) row[0], (String) row[1], (String) row[2], (Date) row[3], (Date) row[4], (BigDecimal) row[5], (boolean) row[6], (String) row[7], (String) row[8], (double) row[9]))
                    .collect(Collectors.toList());
            Long countResultQuery = postRepository.countByIsDeletedFalseAndStatus(Status.APPROVED);

            resultToView.set(postDtoWithFilter, countResultQuery);
            response = new ResponseEntity<>(resultToView, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * radiusInKm: Bán kính để tạo nên bounding box
     * 111.32:  1 vĩ độ có chiều dài xấp xỉ: 111.32 km
     * */
    @Override
    public ResponseEntity<?> findPostsAroundLocation(double latitude, double longitude, double radiusInKm) {
        ResponseEntity<?> response = null;
        try {
            // Tính toán các giá trị min/max cho latitude và longitude dựa trên bán kính
            // Các giá trị được tạo thành 1 hình chữ nhật
            double minLatitude = latitude - (radiusInKm / 111.32);
            double maxLatitude = latitude + (radiusInKm / 111.32);
            double minLongitude = longitude - (radiusInKm / (111.32 * Math.cos(Math.toRadians(latitude))));
            double maxLongitude = longitude + (radiusInKm / (111.32 * Math.cos(Math.toRadians(latitude))));


            // Gọi phương thức của repository để lấy danh sách bài viết trong vùng xung quanh
            List<PostDtoWithFilter> resutlToView = findPostByLatitudeBetweenAndLongitudeBetween(minLatitude, maxLatitude, minLongitude, maxLongitude);
            response = new ResponseEntity<>(resutlToView, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private List<PostDtoWithFilter> findPostByLatitudeBetweenAndLongitudeBetween(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        List<PostDtoWithFilter> result = new ArrayList<>();
        try {
            List<PostEntity> getAllByStatusAndIsDeleted = postRepository.getAllByStatusAndIsDeletedAndRotationIsNotNull(Status.APPROVED, false);
            for (PostEntity post : getAllByStatusAndIsDeleted
            ) {
                String[] rotation = post.getRotation().split(",");
                double latitude = Double.parseDouble(rotation[0]);
                double longitude = Double.parseDouble(rotation[1]);
                if (latitude >= minLatitude && latitude <= maxLatitude &&
                        longitude >= minLongitude && longitude <= maxLongitude) {
                    // Cập nhật thông tin của postDto từ post
                    PostDtoWithFilter postDto = new PostDtoWithFilter();
                    postDto.setId(post.getId());
                    postDto.setTitle(post.getTitle());
                    postDto.setAddress(post.getAddress());
                    postDto.setCreatedDate(post.getCreatedDate());
                    postDto.setModifiedDate(post.getModifiedDate());
                    postDto.setPrice(post.getRoomId().getPrice());
                    postDto.setDeleted(post.isDeleted());
                    postDto.setStatus(post.getStatus().name());
                    postDto.setImage(post.getImageUrlList().get(0));
                    postDto.setAcreage(post.getRoomId().getAcreage());
                    result.add(postDto);
                }
            }
        } catch (Exception ex) {

        }
        return result;
    }
    private String getFileUrls(MultipartFile multipartFile) throws IOException {
        return cloudinary.uploader()
                .upload(multipartFile.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
                .get("url")
                .toString();
    }
}

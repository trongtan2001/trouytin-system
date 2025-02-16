package com.roomster.roomsterbackend.controller.guest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.post.*;
import com.roomster.roomsterbackend.dto.report.ReportDto;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.service.IService.IDatabaseSearch;
import com.roomster.roomsterbackend.service.IService.IPostService;
import com.roomster.roomsterbackend.service.IService.IReportService;
import com.roomster.roomsterbackend.service.IService.IServicePackageService;
import com.roomster.roomsterbackend.service.impl.ProvinceService;
import com.roomster.roomsterbackend.util.extensions.ConvertStringToArrayExtension;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/guest")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestController {

    private final IPostService postService;

    private final IDatabaseSearch iDatabaseSearch;

    private final ProvinceService provinceService;

    private final IReportService reportService;

    private final IServicePackageService servicePackageService;


    @Operation(
            description = "Latitude: Vĩ Độ, Longitude: Kinh Độ",
            summary = "Endpoint For Get Post Around Location"
    )
    @GetMapping("/post/location")
    public ResponseEntity<?> findPostsAroundLocation(@RequestParam double latitude, @RequestParam double longitude) {
        return postService.findPostsAroundLocation(latitude, longitude, 50.0);
    }

    @GetMapping(value = "post/sorted/price")
    public ResponseEntity<?> sortedPostByPriceDes(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                  @RequestParam(name = "size", required = false, defaultValue = "5") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return postService.sortedPostByPriceDes(pageable);
    }

    @GetMapping(value = "post/sorted/acreage")
    public ResponseEntity<?> sortedPostByAcreageDes(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                  @RequestParam(name = "size", required = false, defaultValue = "5") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return postService.sortedPostByAcreageDes(pageable);
    }


    @GetMapping(value = "/service/service-package")
    public ResponseEntity<?> getAllServicePackage(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                  @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return servicePackageService.getAllServicePackage(pageable);
    }
    @GetMapping(value = "/list-post-by-rating")
    public List<PostDtoWithRating> ListPostByRating(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        return postService.getPostByRating(pageable);
    }

    @PostMapping(value = "/post/filters", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SearchResult searchPost(@RequestPart(required = false) String json,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "5") Integer size, Principal connectedUser) throws SQLException {

        Pageable pageable = PageRequest.of(page, size);
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (json != null) {
            try {
                map = objectMapper.readValue(json, LinkedHashMap.class);

                for (var item : map.entrySet()
                ) {
                    if (item.getKey().equals("author_id")) {
                        if (connectedUser != null) {
                            var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
                            map.replace("author_id", user.getId());
                        }
                    }
                }
                ConvertStringToArrayExtension.convertStringToArray(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return iDatabaseSearch.searchFilter(pageable, map);
    }

    @GetMapping(value = "/post/top-province")
    public List<ProvinceDtoWithImage> getTopOfProvince(@RequestParam(name = "page", required = false, defaultValue = "5") Integer page,
                                                       @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ProvinceDto> topOfProvince = postService.getTopOfProvince(pageable);

        return provinceService.setImages(topOfProvince);
    }

    @GetMapping(value = "/postDetail")
    public ResponseEntity<PostDetailDtoWithImage> getPostDetail(@RequestParam Long postId) {
        PostDetailDtoWithImage postDetailDtoWithImage = new PostDetailDtoWithImage();
        try {
            postDetailDtoWithImage.setImages(postService.getPostImages(postId));
            postDetailDtoWithImage.setPostDetail(postService.getPostDetail(postId));
        } catch (Exception ex) {
            ResponseEntity.badRequest();
        }
        return ResponseEntity.ok(postDetailDtoWithImage);
    }

    @PostMapping(value = "/report/add")
    public BaseResponse addReport(@RequestBody ReportDto reportDto) {
        try {
            ReportDto report = reportService.addReport(reportDto);
            if (report != null) {
                return BaseResponse.success("Cảm ơn bạn đã đóng góp ý kiến");
            }
        } catch (Exception ex) {
            return BaseResponse.error("Ex: " + ex);
        }
        return BaseResponse.error("Xin lỗi! Ý kiến của bạn không được chấp nhận");
    }

    @Hidden
    @GetMapping(value = "/post/images")
    public List<PostImageDto> getPostImage(@RequestParam Long postId) {
        return postService.getPostImages(postId);
    }
}

package com.roomster.roomsterbackend.controller.management;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.postType.PostTypeDto;
import com.roomster.roomsterbackend.service.IService.IPostTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/postType")
@PreAuthorize("hasAnyRole('ROLE_MANAGE','ROLE_ADMIN')")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostTypeController {

    private final IPostTypeService postTypeService;

    @GetMapping("/getAll")
    public List<PostTypeDto> getAllPostType(){
        return postTypeService.getAllPostType();
    }

    @PostMapping("/new")
    public BaseResponse addPostType(@RequestBody PostTypeDto postTypeDto){
        return postTypeService.addPostType(postTypeDto);
    }
}

package com.roomster.roomsterbackend.controller.common;

import com.roomster.roomsterbackend.service.IService.IWarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/common/ward")
public class WardController {
    @Autowired
    private IWarnService warnService;

    @GetMapping()
    public ResponseEntity<?> getWardByIdDistrict(@RequestParam(value="id_district", defaultValue="0") Long district_Id) {
        return warnService.findByIdCity(district_Id);
    }
}

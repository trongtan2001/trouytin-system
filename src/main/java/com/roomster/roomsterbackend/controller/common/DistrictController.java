package com.roomster.roomsterbackend.controller.common;

import com.roomster.roomsterbackend.service.IService.IDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/common/district")
public class DistrictController {
    @Autowired
    private IDistrictService districtService;

    @GetMapping()
    public ResponseEntity<?> getDistrictByIdCity(@RequestParam(value="id_city", defaultValue="0") Long city_Id) {
        return districtService.findByIdCity(city_Id);
    }
}

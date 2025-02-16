package com.roomster.roomsterbackend.controller.ultiMagener;


import com.roomster.roomsterbackend.dto.admin.HouseDto;
import com.roomster.roomsterbackend.service.IService.ultiManager.IHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/room-master/house")
@PreAuthorize("hasRole('ROLE_ULTI_MANAGER')")
public class HouseController {

    @Autowired
    private IHouseService houseService;

    @GetMapping()
    public ResponseEntity<?> getAllHouses(@RequestParam(defaultValue = "0", required = false) String price,
                                          @RequestParam(defaultValue = "0", required = false) String acreage,
                                          @RequestParam(defaultValue = "0", required = false) String stayMax,
                                          @RequestParam(required = false) String status) {
        return houseService.getAllHouses(price, acreage, stayMax, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable String id) {
        return houseService.getHouseById(id);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatusHouseById() {
        return houseService.getStatusHouse();
    }

    @PostMapping()
    public ResponseEntity<?> createHouse(@RequestBody HouseDto house) {
        return houseService.createHouse(house);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHouse(@PathVariable String id, @RequestBody HouseDto house) {
        return houseService.updateHouse(id, house);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable String id) {
        return houseService.deleteHouse(id);
    }
}


package com.roomster.roomsterbackend.controller.ultiMagener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roomster.roomsterbackend.entity.TenantEntity;
import com.roomster.roomsterbackend.service.IService.ultiManager.ITenantService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/room-master/tenant")
@PreAuthorize("hasRole('ROLE_ULTI_MANAGER')")
public class TenantController {

    @Autowired
    private ITenantService tenantService;

    @GetMapping
    public ResponseEntity<?> getAllTenants() {
        return tenantService.getAllTenant();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable String id) {
        return tenantService.getGuestById(id);
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<?> getTenantsByRoomId(@PathVariable String id) {
        return tenantService.getTenantByRoomId(id);
    }
    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody TenantEntity tenant) {
        return tenantService.createTenant(tenant);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenant(@PathVariable String id, @RequestBody TenantEntity tenant) {
        return tenantService.updateTenant(id, tenant);
    }
    @PutMapping("/move/{id}")
    public ResponseEntity<?> moveTenant(@PathVariable String id, @RequestBody List<String> tenantIds) {
        return tenantService.moveTenant(id, tenantIds);
    }
    @DeleteMapping()
    public ResponseEntity<?> deleteTenant(@RequestBody List<String> tenantIds) {
        return tenantService.deleteTenant(tenantIds);
    }
}
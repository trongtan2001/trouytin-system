package com.roomster.roomsterbackend.controller.admin;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.auth.RegisterRequest;
import com.roomster.roomsterbackend.dto.service.servicePackage.ServicePackageDto;
import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.service.IService.*;
import com.roomster.roomsterbackend.service.IService.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {
    private final IPostService postService;

    private final IServicePackageService servicePackageService;

    private final IUserService userService;

    private final IAuthenticationService authenticationService;

    private final IReportService reportService;

    private final IPaymentService paymentService;

    private final IRoleService roleService;

    private final ITransactionService transactionService;

    //TODO: Dashboard
    @GetMapping(value = "transaction/status")
    public ResponseEntity<?> getTotalPaymentServiceByMonth() {
        return postService.getTotalPaymentServiceByMonth();
    }

    @GetMapping(value = "payment/status")
    public ResponseEntity<?> getTotalPaymentTransactionByMonth() {
        return paymentService.getTotalPaymentTransactionByMonth();
    }

    @GetMapping(value = "user/status")
    public ResponseEntity<?> getUserAccountStatus(){
        return userService.getUserAccountStatus();
    }

    @GetMapping(value = "post/status")
    public ResponseEntity<?> getStatusPost() {
        return postService.getStatusPost();
    }

    //TODO: CRUD service package

    @PostMapping(value = "/service/add-service-package")
    public ResponseEntity<?> addServicePackage(@RequestBody ServicePackageDto request) {
        return servicePackageService.addServicePackage(request);
    }

    @PutMapping(value = "/service/update-service-package/{id}")
    public ResponseEntity<?> updateServicePackage(@PathVariable Long id, @RequestBody ServicePackageDto request) {
        return servicePackageService.updateServicePackage(id, request);
    }

    @DeleteMapping(value = "/service/delete-service-package/{id}")
    public ResponseEntity<?> deleteServicePackage(@PathVariable Long id) {
        return servicePackageService.removeServicePackageById(id);
    }

    //TODO: Transaction service package by user

    @GetMapping("transaction/service-package")
    public ResponseEntity<?> getAllTransactionServicePackage(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                             @RequestParam(name = "size", required = false, defaultValue = "5") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return transactionService.getAllTransactionService(pageable);
    }

    //TODO: CRUD Post

    @PatchMapping(value = "/setIsApprovedPost")
    public BaseResponse setIsApprovedPosts(Long[] listPostId) {
        try {
            postService.setIsApprovedPosts(listPostId);
        } catch (Exception ex) {
            return BaseResponse.error("Ex: " + ex.getMessage());
        }
        return BaseResponse.success("Bài viết cập nhật thành công");
    }

    @PatchMapping(value = "/setIsRejectedPost")
    public BaseResponse setIsRejectedPosts(Long[] listPostId) {
        try {
            postService.setIsRejectedPosts(listPostId);
        } catch (Exception ex) {
            return BaseResponse.error("Ex: " + ex.getMessage());
        }
        return BaseResponse.success("Bài viết cập nhật thành công");
    }


    //TODO: CRUD Account User

    @GetMapping(value = "/user/getAll")
    public ResponseEntity<?> getAllAccountByDeletedFalse(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                         @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.findAllByIsDeletedIsFalse(pageable);
    }

    @GetMapping(value = "/user/getAllByDeleted")
    public ResponseEntity<?> getAllAccountByDeletedTrue(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.findAllByIsDeletedIsTrue(pageable);
    }

    @GetMapping(value = "/user/by-role-name")
    public ResponseEntity<?> getAllAccountByRoleName(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                                     @RequestParam String roleName
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.findAllAccountByRoleName(pageable, roleName);
    }

    @DeleteMapping(value = "/user/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam Long[] listId) {
        return userService.deleteUserByIds(listId);
    }

    @PostMapping(value = "/user/register-by-admin")
    public ResponseEntity<?> registerByAdmin(@RequestBody RegisterRequest request) {
        return authenticationService.registerByAdmin(request);
    }

    @PatchMapping(value = "/user/up-role-account")
    public ResponseEntity<?> registerByAdmin(@RequestParam String roleName, @RequestParam Long userId) {
        return userService.upRoleUserByAdmin(roleName, userId);
    }

    //TODO: CRUD Report

    @GetMapping(value = "/report/getAll")
    public ResponseEntity<?> getAllReport(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.getAllReport(pageable);
    }

    @DeleteMapping(value = "/report/delete")
    public ResponseEntity<?> deleteReportByIds(@RequestParam Long[] ids) {
        return reportService.deleteReportByIds(ids);
    }

    @GetMapping("/payment/getAll")
    public ResponseEntity<?> getAllPayment(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                           @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentService.getAllPayment(pageable);
    }

    //TODO: CRUD Role
    @GetMapping(value = "/role/getAll")
    public ResponseEntity<?> getAllRole() {
        return roleService.getAll();
    }

    @PostMapping(value = "/role")
    public ResponseEntity<?> addRole(@RequestBody RoleEntity entity) {
        return roleService.addRole(entity);
    }


}

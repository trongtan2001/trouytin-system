package com.roomster.roomsterbackend.service.IService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface ITransactionService {
    ResponseEntity<?> purchasePackageByUser(Principal connectedUser, Long servicePackageId);

    ResponseEntity<?> isValidUltiManager(Principal connectedUser);

    ResponseEntity<?> purchasedServiceByUser();

    ResponseEntity<?> getAllTransactionServiceByUser(Principal connectedUser, Pageable pageable);

    ResponseEntity<?> getAllTransactionService(Pageable pageable);
}

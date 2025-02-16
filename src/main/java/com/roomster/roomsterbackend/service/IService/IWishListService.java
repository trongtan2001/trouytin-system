package com.roomster.roomsterbackend.service.IService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface IWishListService {
    ResponseEntity<?> addPostToWishlist(Principal connectedUser, Long postId, String wishListName);

    ResponseEntity<?> getWishListByNameAndUser(Principal connectedUser, String wishListName, Pageable pageable);
    ResponseEntity<?> deleteWishListItem(Long wishListItemId);
}

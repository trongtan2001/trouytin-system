package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.entity.PostEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.entity.WishlistEntity;
import com.roomster.roomsterbackend.entity.WishlistItemEntity;
import com.roomster.roomsterbackend.repository.PostRepository;
import com.roomster.roomsterbackend.repository.WishListItemRepository;
import com.roomster.roomsterbackend.repository.WishListRepository;
import com.roomster.roomsterbackend.service.IService.IWishListService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class WishListService implements IWishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private WishListItemRepository wishListItemRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public ResponseEntity<?> addPostToWishlist(Principal connectedUser, Long postId, String wishListName) {
        ResponseEntity<?> responseEntity = null;
        try {
            var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();

            if(user != null){
                var exitWishListItem = wishListRepository.wishListItemExit(wishListName,postId, user.getId());
                if(exitWishListItem == null){
                    WishlistEntity wishlist = wishListRepository.findByUserWishList_IdAndWishlistName(user.getId(), wishListName);
                    if(wishlist == null){
                        wishlist = new WishlistEntity();
                        wishlist.setWishlistName(wishListName);
                        wishlist.setUserWishList(user);
                        wishlist = wishListRepository.save(wishlist);
                    }
                    Optional<PostEntity> post = postRepository.findById(postId);
                    if(post.isPresent()){
                        WishlistItemEntity wishlistItem = new WishlistItemEntity();
                        wishlistItem.setWishlist(wishlist);
                        wishlistItem.setPostId(post.get().getId());
                        // add wishlist item to wishlist
                        wishlist.getWishlistItems().add(wishlistItem);
                        wishListItemRepository.save(wishlistItem);
                        responseEntity = new ResponseEntity<>(wishlist,HttpStatus.OK);
                    }else {
                        responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_POST_NOT_FOUND),HttpStatus.NOT_FOUND);
                    }
                }else{
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_WISHLIST_ITEM_EXITED), HttpStatus.BAD_REQUEST);
                }
            }else{
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getWishListByNameAndUser(Principal connectedUser, String wishListName, Pageable pageable) {
        ResponseEntity<?> responseEntity = null;
        try {
            var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();
            if(user != null){
                var wishList = wishListRepository.getAllWishListByNameAndUser(wishListName, user.getId(),pageable);
                 responseEntity = new ResponseEntity<>(wishList, HttpStatus.OK);
            }

        }catch (Exception ex){
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> deleteWishListItem(Long wishListItemId) {
        ResponseEntity<?> responseEntity = null;
        try {
            Optional<WishlistItemEntity> wishlistItem = wishListItemRepository.findById(wishListItemId);
            wishListItemRepository.deleteById(wishListItemId);
            if(wishlistItem.isPresent()) {
                List<WishlistItemEntity> wishListItems = wishListItemRepository.findAllByWishlist_WishlistId(wishlistItem.get().getWishlist().getWishlistId());
                if (wishListItems.isEmpty()) {
                    wishlistItem.ifPresent(wishlistItemEntity -> wishListRepository.deleteById(wishlistItemEntity.getWishlist().getWishlistId()));
                }
            }
            responseEntity = new ResponseEntity<>(BaseResponse.success("Bạn đã xóa thành công !"), HttpStatus.OK);

        }catch (Exception ex){
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}

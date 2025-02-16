package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.WishlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListItemRepository extends JpaRepository<WishlistItemEntity, Long> {
    List<WishlistItemEntity> findAllByWishlist_WishlistId(Long wishListId);
}

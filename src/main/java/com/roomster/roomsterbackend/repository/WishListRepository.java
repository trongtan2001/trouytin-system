package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.dto.post.PostByWishList;
import com.roomster.roomsterbackend.dto.wishlist.ExitWishlist;
import com.roomster.roomsterbackend.entity.WishlistEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishlistEntity, Long> {
    WishlistEntity findByUserWishList_IdAndWishlistName(Long id, String wishlistName);

    @Query(value = "select w.wishlist_name as wishListName, wi.wishlist_item_id as wishListItem, wi.post_id as postId, w.user_id as userId from wishlists as w\n" +
            "inner join wishlist_items as wi\n" +
            "on w.wishlist_id = wi.wishlist_id\n" +
            "where w.wishlist_name =:wishListName and w.user_id =:userId and wi.post_id =:postId", nativeQuery = true)
    ExitWishlist wishListItemExit(@Param("wishListName") String wishListName,@Param("postId") Long postId,@Param("userId") Long userId);

    @Query(value = "Select p.id, p.title, p.address, p.created_date as createdDate, i.price, max(pimg.image_url_list) as image, wi.wishlist_item_id as wishListItemId\n" +
            "from posts p\n" +
            "inner join infor_rooms i on p.room_id = i.id\n" +
            "inner join post_entity_image_url_list pimg on pimg.post_entity_id = p.id\n" +
            "inner join wishlist_items as wi on p.id = wi.post_id\n" +
            "inner join wishlists as w on w.wishlist_id = wi.wishlist_id\n" +
            "where w.user_id =:userId and w.wishlist_name LIKE %:wishListName%\n" +
            "group by p.id, p.title, p.address, p.created_date, i.price, p.is_deleted, wi.wishlist_item_id\n" +
            "order by wi.wishlist_item_id desc", nativeQuery = true)
    List<PostByWishList> getAllWishListByNameAndUser(@Param("wishListName") String wishListName, @Param("userId") Long userId, Pageable pageable);
}

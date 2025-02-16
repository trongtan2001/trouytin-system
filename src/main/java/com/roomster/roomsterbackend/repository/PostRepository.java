package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.dto.post.PostDetailDto;
import com.roomster.roomsterbackend.dto.post.PostDtoWithRating;
import com.roomster.roomsterbackend.dto.post.PostImageDto;
import com.roomster.roomsterbackend.dto.post.ProvinceDto;
import com.roomster.roomsterbackend.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

//    List<PostEntity> findByLatitudeBetweenAndLongitudeBetween(
//            Double minLatitude, Double maxLatitude,
//            Double minLongitude, Double maxLongitude
//    );

    @Query(value = "Select p.id, p.title, p.address, p.created_date,p.modified_date, ir.price, p.is_deleted,p.status, max(pimg.image_url_list) as image , ir.acreage from posts p left join post_entity_image_url_list pimg on p.id = pimg.post_entity_id\n" +
            "inner join infor_rooms ir on ir.id = p.room_id \n" +
            "where p.is_deleted = false and p.status = \"APPROVED\"\n" +
            "group by p.id, p.title, p.address, p.created_by, p.created_date, ir.price, p.is_deleted \n" +
            "order by ir.price ASC", nativeQuery = true)
    List<Object[]> getAllByOrderPriceDesc(Pageable pageable);

    @Query(value = "Select p.id, p.title, p.address, p.created_date,p.modified_date, ir.price, p.is_deleted,p.status, max(pimg.image_url_list) as image, ir.acreage\n" +
            "from posts p left join post_entity_image_url_list pimg on p.id = pimg.post_entity_id\n" +
            "inner join infor_rooms ir on ir.id = p.room_id \n" +
            "where p.is_deleted = false and p.status = \"APPROVED\"\n" +
            "group by p.id, p.title, p.address, p.created_by, p.created_date, ir.price, p.is_deleted, ir.acreage\n" +
            "order by ir.acreage DESC", nativeQuery = true)
    List<Object[]> getAllByOrderAcreageDesc(Pageable pageable);


    List<PostEntity> getPostEntityByAuthorId(Pageable pageable, Long id);

    @Query(value = "Select p.id, p.title, p.address, p.created_date as createdDate, i.price, p.is_deleted as isDeleted, max(pimg.image_url_list) as image, AVG(r.star_point) as averageRating\n" +
            "from posts p\n" +
            "left join ratings r on p.id = r.post_id\n" +
            "inner join infor_rooms i on p.room_id = i.id\n" +
            "inner join post_entity_image_url_list pimg on pimg.post_entity_id = p.id\n" +
            "where p.status like 'Approved'\n" +
            "group by p.id\n" +
            "Order by averageRating desc", nativeQuery = true)
    List<PostDtoWithRating> getPostByRating(Pageable pageable);

    @Query(value = "SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(address, ',', -1), ',', 1)) AS provinceName, COUNT(*) AS totalPosts \n" +
            "FROM  posts \n" +
            "where posts.is_deleted = false and posts.status like 'Approved' \n" +
            "GROUP BY provinceName ORDER BY totalPosts DESC", nativeQuery = true)
    List<ProvinceDto> getTopOfProvince(Pageable pageable);

    @Query(value = "select p.id, p.title, p.address, p.description, p.object, p.convenient, p.surroundings, p.post_type_id as postType, p.created_by as createdBy, p.created_date as createdDate, p.rotation, ir.acreage, ir.electricity_price as electricityPrice, ir.price, ir.staymax, ir.water_price as waterPrice, ir.empty_room as emptyRoom, ir.number_room as numberRoom, ir.id as inforRoomId\n" +
            "from posts p\n" +
            "left join infor_rooms ir\n" +
            "on p.room_id = ir.id\n" +
            "where p.id =:postId\n" +
            "group by p.id, p.title, p.address, p.description, p.object, p.convenient, p.surroundings, p.post_type_id, p.created_by, p.created_date, p.rotation, ir.acreage, ir.electricity_price, ir.price, ir.staymax, ir.water_price", nativeQuery = true)
    Optional<PostDetailDto> getPostDetail(@Param("postId") Long postId);


    @Query(value = "select img.image_url_list as image from post_entity_image_url_list img where img.post_entity_id =:postId", nativeQuery = true)
    List<PostImageDto> getPostImages(@Param("postId") Long postId);
    List<PostEntity> getAllByStatusAndIsDeletedAndRotationIsNotNull(Status status, boolean isDeleted);
    List<PostEntity> getAllByStatusAndIsDeleted(Pageable pageable, Status status, boolean isDeleted);

    Long countByIsDeletedFalse();
    Long countByIsDeletedFalseAndStatus(Status status);

    Long countByStatus(Status status);

    @Query(value = "SELECT Month(t.purchase_date) as month, SUM(sp.price) as tolalPrice \n" +
            "FROM transaction as t inner join service_package as sp\n" +
            "where t.package_id = sp.id\n" +
            "GROUP BY MONTH(t.purchase_date)\n" +
            "Order by month(t.purchase_date)", nativeQuery = true)
    List<Object[]> getTotalPaymentServiceByMonth();
}

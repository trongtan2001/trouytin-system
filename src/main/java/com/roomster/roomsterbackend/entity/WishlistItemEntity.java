package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "wishlist_items")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class WishlistItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_item_id")
    private Long wishlistItemId;

    @ManyToOne()
    @JoinColumn(name = "wishlist_id", referencedColumnName = "wishlist_id")
    @JsonBackReference
    private WishlistEntity wishlist;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "created_date",
            nullable = false,
            updatable = false)
    @CreatedDate
    private Date createdDate;

    // Constructors, getters, and setters
}

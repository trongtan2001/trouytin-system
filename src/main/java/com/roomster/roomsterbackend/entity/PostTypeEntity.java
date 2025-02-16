package com.roomster.roomsterbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "post_types")
public class PostTypeEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "postType")
    private List<PostEntity> posts;
}

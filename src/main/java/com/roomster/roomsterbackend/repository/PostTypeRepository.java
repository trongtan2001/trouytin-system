package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.PostTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PostTypeRepository extends JpaRepository<PostTypeEntity, Long> {
    PostTypeEntity getPostEntityByName(String postTypeName);
    PostTypeEntity getPostEntityById(Long id);
    PostTypeEntity getPostEntityByCode(String postTypeCode);

}

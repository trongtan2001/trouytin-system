package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String roleName);
    RoleEntity getRoleEntityById(Long id);
}

package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    List<UserEntity> findAllByIsDeletedTrue(Pageable pageable);
    Long countAllByIsDeletedTrue();
    List<UserEntity> findAllByIsDeletedFalse(Pageable pageable);
    List<UserEntity> findAllByRolesNotContainingAndIsDeletedFalse(RoleEntity role);
    Long countAllByIsDeletedFalse();
    boolean existsByPhoneNumber(String phoneNumber);

    List<UserEntity> findAllByRoles_NameAndIsDeletedFalse(String roleName);

    Long countByRoles_NameAndIsDeletedFalse(String roleName);


    boolean existsAllByPhoneNumber (String phoneNumber);

    @Query(value = "SELECT * FROM users " +
            "WHERE id=:userId", nativeQuery = true)
    Optional<UserEntity> getUserEntityByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "delete from user_role where user_id =:userId and role_id =:roleId",
            nativeQuery = true)
    void deleteRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> getAllByPostId_Id(Long postId);
}

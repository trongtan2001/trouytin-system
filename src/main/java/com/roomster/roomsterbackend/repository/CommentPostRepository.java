package com.roomster.roomsterbackend.repository;

import com.roomster.roomsterbackend.entity.CommentEnity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentPostRepository extends JpaRepository<CommentEnity, Long> {
    List<CommentEnity> getCommentByPostId(Long postId);
}

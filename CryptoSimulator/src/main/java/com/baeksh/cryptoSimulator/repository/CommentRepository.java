package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.Comment;
import com.baeksh.cryptoSimulator.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByPost(CommunityPost post);  // 특정 게시물의 댓글 조회
}

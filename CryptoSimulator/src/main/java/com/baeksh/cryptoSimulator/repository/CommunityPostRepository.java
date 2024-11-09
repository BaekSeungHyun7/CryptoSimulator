package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  List<CommunityPost> findByOrderByIsNoticeDescCreatedAtDesc();  // 공지 글을 최상단에 정렬
}

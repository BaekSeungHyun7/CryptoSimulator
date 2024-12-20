package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  Page<CommunityPost> findAllByOrderByIsNoticeDescCreatedAtDesc(Pageable pageable);
}

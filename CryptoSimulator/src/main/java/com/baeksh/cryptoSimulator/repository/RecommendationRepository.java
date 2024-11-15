package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.CommunityPost;
import com.baeksh.cryptoSimulator.entity.Recommendation;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
  // 특정 게시물에 대해 특정 사용자가 추천했는지 확인
  Optional<Recommendation> findByPostAndUser(CommunityPost post, UserEntity user);
}

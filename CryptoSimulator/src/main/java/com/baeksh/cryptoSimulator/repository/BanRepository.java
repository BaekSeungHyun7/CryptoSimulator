package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.Ban;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BanRepository extends JpaRepository<Ban, Long> {
  Optional<Ban> findByUser(UserEntity user);  // 특정 사용자의 차단 여부 확인
}

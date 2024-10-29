package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  // 사용자 이름으로 사용자 조회
  Optional<UserEntity> findByUsername(String username);
  // 이메일로 사용자 조회
  Optional<UserEntity> findByEmail(String email);
  
  // 사용자 이름 중복 여부 확인
  boolean existsByUsername(String username);
  // 이메일 중복 여부 확인
  boolean existsByEmail(String email);
}

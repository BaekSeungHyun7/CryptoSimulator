package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);  // 사용자 이름으로 사용자 조회
    Optional<UserEntity> findByEmail(String email);  // 이메일로 사용자 조회
}

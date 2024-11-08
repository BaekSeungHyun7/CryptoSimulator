package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.PortfolioEntity;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
  //사용자 ID로 포트폴리오 조회
  List<PortfolioEntity> findByUser(UserEntity user);
  
  //유저별 코인 보유 여부
  Optional<PortfolioEntity> findByUserAndCryptoSymbol(UserEntity user, String cryptoSymbol);
  
  //사용자 포트폴리오 삭제
  void deleteByUser(UserEntity user);
}

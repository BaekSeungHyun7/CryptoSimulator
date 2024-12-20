package com.baeksh.cryptoSimulator.repository;

import com.baeksh.cryptoSimulator.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
  
}

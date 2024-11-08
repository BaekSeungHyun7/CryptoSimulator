package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.repository.PortfolioRepository;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceTest.class);

  @Mock
  private PortfolioRepository portfolioRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private PortfolioService portfolioService;

  private UserEntity user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = UserEntity.builder()
      .userId(1L)
      .username("testUser")
      .password(new BCryptPasswordEncoder().encode("password"))
      .balance(500000.0)
      .debtRefTime(LocalDateTime.now().minusHours(24))
      .build();
  }

  @Test
  void testInitializeSeedMoneyAllowed() {
    logger.info("24시간 경과 후 시드머니 발급 테스트");
    user.setBalance(900000.0);
    user.setDebtRefTime(LocalDateTime.now().minusDays(1));

    portfolioService.initializePortfolio(user.getUserId());

    assertEquals(1000000.0, user.getBalance());
    verify(portfolioRepository, times(1)).deleteByUser(user);
    verify(userRepository, times(1)).save(user);
    logger.info("시드머니 발급 후 잔액 확인: {}", user.getBalance());
  }

  @Test
  void testInitializeSeedMoneyNotAllowed() {
    logger.info("24시간 이내 시드머니 재발급 불가 테스트");
    user.setBalance(900000.0);
    user.setDebtRefTime(LocalDateTime.now());

    CustomException exception = assertThrows(CustomException.class, () -> {
      portfolioService.initializePortfolio(user.getUserId());
    });

    assertEquals(ErrorCode.SEED_MONEY_ALREADY_ISSUED, exception.getErrorCode());
    logger.info("시드머니 재발급 제한 예외 발생 확인됨");
  }

  @Test
  void testCalculateProfitPercentage() {
    logger.info("수익률 계산 테스트");
    BigDecimal avgPrice = BigDecimal.valueOf(30000000);
    BigDecimal currentPrice = BigDecimal.valueOf(33000000);

    String profitPercentage = portfolioService.calculateProfitPercentage(avgPrice, currentPrice);

    assertEquals("+10.00%", profitPercentage);
    logger.info("수익률 계산 결과: {}", profitPercentage);
  }
}



package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.TickerDTO;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.repository.PortfolioRepository;
import com.baeksh.cryptoSimulator.repository.TransactionRepository;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceTest.class); // Logger 추가

  @Mock
  private PortfolioRepository portfolioRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CryptocurrencyService cryptocurrencyService;

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
        .balance(500000.0) // 50만원 보유중
        .build();
    
  }
  
  @Test
  void testBuyCryptoInsufficientFunds() {
    logger.info("잔액이 부족한 경우 매수 실패");
    // 가상화폐의 현재 가격을 설정
    when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
    .thenReturn(Collections.singletonList(new TickerDTO("KRW-BTC", "BTC", "비트코인", 60000000, 0, 0, 0)));

    // 잔액 부족으로 예외 발생 여부 확인
    CustomException exception = assertThrows(CustomException.class, () -> {
      portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "BUY");
      
    });
    
    assertEquals(ErrorCode.INSUFFICIENT_FUNDS, exception.getErrorCode());
    logger.info("예외처리: {}", exception.getErrorCode().getMessage());
    verify(cryptocurrencyService, times(1)).fetchTickerData("KRW-BTC");
    
  }

  @Test
  void testBuyCryptoSuccessful() {
    logger.info("잔액이 충분히 있는 경우에만 매수 성공");

    // 가상화폐의 현재 가격을 설정 (잔액 충분한 경우)
    when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
    .thenReturn(Collections.singletonList(new TickerDTO("KRW-BTC", "BTC", "비트코인", 30000000, 0, 0, 0)));

    portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "BUY");

    assertEquals(470000.0, user.getBalance(), 0.1); // 잔액이 매수 금액만큼 감소했는지 확인
    logger.info("구매 후 잔액: {}", user.getBalance());
    verify(cryptocurrencyService, times(1)).fetchTickerData("KRW-BTC");
    verify(portfolioRepository, times(1)).save(any());
    verify(transactionRepository, times(1)).save(any());
    
  }

  @Test
  void testSellCryptoSuccessful() {
    logger.info("가상화폐 매수 성공");
    
    // 가상화폐의 현재 가격을 설정
    when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
    .thenReturn(Collections.singletonList(new TickerDTO("KRW-BTC", "BTC", "비트코인", 30000000, 0, 0, 0)));

    
    // 포폴 업뎃
    portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "BUY");
    portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "SELL");

    assertEquals(500000.0, user.getBalance(), 0.1); // 잔액이 원상 복구되었는지 확인용
    logger.info("매도 후 : {}", user.getBalance());
    
    verify(cryptocurrencyService, times(2)).fetchTickerData("KRW-BTC");
    verify(portfolioRepository, times(2)).save(any());
    verify(transactionRepository, times(2)).save(any());
    
  }
}


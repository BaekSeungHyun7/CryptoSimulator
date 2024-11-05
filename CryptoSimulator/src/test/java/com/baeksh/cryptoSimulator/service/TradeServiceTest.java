package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.TickerDTO;
import com.baeksh.cryptoSimulator.dto.TradeRequestDto;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.entity.TransactionType;  // TransactionType import 추가
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
class TradeServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(TradeServiceTest.class);

  @Mock
  private PortfolioRepository portfolioRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CryptocurrencyService cryptocurrencyService;

  @InjectMocks
  private TradeService tradeService;

  private UserEntity user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = UserEntity.builder()
      .userId(1L)
      .username("testUser")
      .password(new BCryptPasswordEncoder().encode("password"))
      .balance(500000.0)
      .build();
  }

  @Test
  void testBuyCryptoInsufficientFunds() {
    logger.info("잔액이 부족한 경우 매수 실패 테스트");

    TickerDTO tickerDTO = new TickerDTO();
    tickerDTO.setMarket("KRW-BTC");
    tickerDTO.setSymbol("BTC");
    tickerDTO.setKoreanName("비트코인");
    tickerDTO.setTradePrice(95000000.0);
    tickerDTO.setAccTradePrice(0);
    tickerDTO.setAccTradeVolume24h(0);
    tickerDTO.setSignedChangeRate(0);

    when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
      .thenReturn(Collections.singletonList(tickerDTO));

    TradeRequestDto request = new TradeRequestDto("KRW-BTC", 0.01, TransactionType.BUY);
    CustomException exception = assertThrows(CustomException.class, () -> {
      tradeService.executeTrade(user.getUserId(), request);
    });

    assertEquals(ErrorCode.INSUFFICIENT_FUNDS, exception.getErrorCode());
    verify(cryptocurrencyService, times(1)).fetchTickerData("KRW-BTC");
  }

  @Test
  void testBuyCryptoSuccessful() {
    logger.info("잔액이 충분한 경우 매수 성공 테스트");

    TickerDTO tickerDTO = new TickerDTO();
    tickerDTO.setMarket("KRW-BTC");
    tickerDTO.setSymbol("BTC");
    tickerDTO.setKoreanName("비트코인");
    tickerDTO.setTradePrice(95000000.0);
    tickerDTO.setAccTradePrice(0);
    tickerDTO.setAccTradeVolume24h(0);
    tickerDTO.setSignedChangeRate(0);

    when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
      .thenReturn(Collections.singletonList(tickerDTO));

    TradeRequestDto request = new TradeRequestDto("KRW-BTC", 0.01, TransactionType.BUY);
    tradeService.executeTrade(user.getUserId(), request);

    assertEquals(470000.0, user.getBalance(), 0.1);
    verify(cryptocurrencyService, times(1)).fetchTickerData("KRW-BTC");
    verify(portfolioRepository, times(1)).save(any());
    verify(transactionRepository, times(1)).save(any());
  }

  @Test
  void testSellCryptoSuccessful() {
    logger.info("가상화폐 매도 성공 테스트");

    TickerDTO tickerDTO = new TickerDTO();
    tickerDTO.setMarket("KRW-BTC");
    tickerDTO.setSymbol("BTC");
    tickerDTO.setKoreanName("비트코인");
    tickerDTO.setTradePrice(95000000.0);
    tickerDTO.setAccTradePrice(0);
    tickerDTO.setAccTradeVolume24h(0);
    tickerDTO.setSignedChangeRate(0);

    when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
      .thenReturn(Collections.singletonList(tickerDTO));

    TradeRequestDto buyRequest = new TradeRequestDto("KRW-BTC", 0.01, TransactionType.BUY);
    TradeRequestDto sellRequest = new TradeRequestDto("KRW-BTC", 0.01, TransactionType.SELL);  

    tradeService.executeTrade(user.getUserId(), buyRequest);
    tradeService.executeTrade(user.getUserId(), sellRequest);

    assertEquals(500000.0, user.getBalance(), 0.1);
    verify(cryptocurrencyService, times(2)).fetchTickerData("KRW-BTC");
    verify(portfolioRepository, times(2)).save(any());
    verify(transactionRepository, times(2)).save(any());
  }
}



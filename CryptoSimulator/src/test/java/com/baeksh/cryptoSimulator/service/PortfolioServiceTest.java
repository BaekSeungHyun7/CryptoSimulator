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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceTest.class);

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
                .balance(500000.0) // 50만 원 보유 중
                .debtRefTime(LocalDateTime.now().minusDays(1)) // 마지막 시드머니 발급 24시간 경과
                .build();
    }

    @Test
    void testBuyCryptoInsufficientFunds() {
        logger.info("잔액이 부족한 경우 매수 실패 테스트");
        when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
                .thenReturn(Collections.singletonList(new TickerDTO("KRW-BTC", "BTC", "비트코인", 60000000, 0, 0, 0)));

        CustomException exception = assertThrows(CustomException.class, () -> {
            portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "BUY");
        });

        assertEquals(ErrorCode.INSUFFICIENT_FUNDS, exception.getErrorCode());
        logger.info("예외 발생 확인: {}", exception.getErrorCode().getMessage());
        verify(cryptocurrencyService, times(1)).fetchTickerData("KRW-BTC");
    }

    @Test
    void testBuyCryptoSuccessful() {
        logger.info("잔액이 충분한 경우 매수 성공 테스트");
        when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
                .thenReturn(Collections.singletonList(new TickerDTO("KRW-BTC", "BTC", "비트코인", 30000000, 0, 0, 0)));

        portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "BUY");

        assertEquals(470000.0, user.getBalance(), 0.1);
        logger.info("매수 후 잔액 확인: {}", user.getBalance());
        verify(cryptocurrencyService, times(1)).fetchTickerData("KRW-BTC");
        verify(portfolioRepository, times(1)).save(any());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testSellCryptoSuccessful() {
        logger.info("가상화폐 매도 성공 테스트");

        when(cryptocurrencyService.fetchTickerData("KRW-BTC"))
                .thenReturn(Collections.singletonList(new TickerDTO("KRW-BTC", "BTC", "비트코인", 30000000, 0, 0, 0)));

        portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "BUY");
        portfolioService.updatePortfolio(user, "KRW-BTC", 0.01, "SELL");

        assertEquals(500000.0, user.getBalance(), 0.1);
        logger.info("매도 후 잔액 확인: {}", user.getBalance());

        verify(cryptocurrencyService, times(2)).fetchTickerData("KRW-BTC");
        verify(portfolioRepository, times(2)).save(any());
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void testInitializeSeedMoneyAllowed() {
        logger.info("24시간 경과 후 시드머니 발급 테스트");
        user.setBalance(900000.0); // 잔액이 100만 원 미만으로 설정
        user.setDebtRefTime(LocalDateTime.now().minusDays(1)); // 마지막 발급 이후 24시간 경과

        portfolioService.initializePortfolio(user);

        assertEquals(1000000.0, user.getBalance());
        verify(portfolioRepository, times(1)).deleteByUser(user);
        verify(userRepository, times(1)).save(user);
        logger.info("시드머니 발급 후 잔액 확인: {}", user.getBalance());
    }

    @Test
    void testInitializeSeedMoneyNotAllowed() {
        logger.info("24시간 이내 시드머니 재발급 불가 테스트");
        user.setBalance(900000.0); // 잔액이 100만 원 미만으로 설정
        user.setDebtRefTime(LocalDateTime.now()); // 마지막 발급 이후 24시간 경과하지 않음

        CustomException exception = assertThrows(CustomException.class, () -> {
            portfolioService.initializePortfolio(user);
        });

        logger.info("시드머니 재발급 제한 예외 발생 확인됨");
    }

    @Test
    void testCalculateProfitPercentage() {
        logger.info("수익률 계산 테스트");

        BigDecimal avgPrice = BigDecimal.valueOf(30000000); // 매수 평균가
        BigDecimal currentPrice = BigDecimal.valueOf(33000000); // 현재 시세

        String profitPercentage = portfolioService.calculateProfitPercentage(avgPrice, currentPrice);

        assertEquals("+10.00%", profitPercentage);
        logger.info("수익률 계산 결과: {}", profitPercentage);
    }
}



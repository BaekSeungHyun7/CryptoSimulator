package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.PortfolioDto;
import com.baeksh.cryptoSimulator.entity.PortfolioEntity;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.message.Message;
import com.baeksh.cryptoSimulator.repository.PortfolioRepository;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.text.DecimalFormat;


@Service
@RequiredArgsConstructor
public class PortfolioService {

  private final PortfolioRepository portfolioRepository;
  private final UserRepository userRepository;
  private final CryptocurrencyService cryptocurrencyService;

  // 초기 시드머니 발급
  public String initializePortfolio(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (user.getBalance() > 1_000_000) {
      throw new CustomException(ErrorCode.SEED_MONEY_NOT_ALLOWED);
    }

    if (user.getDebtRefTime() != null && user.getDebtRefTime().isAfter(LocalDateTime.now().minusHours(24))) {
      throw new CustomException(ErrorCode.SEED_MONEY_ALREADY_ISSUED);
    }

    user.setBalance(1_000_000);
    user.setDebtRefTime(LocalDateTime.now());
    clearPortfolio(user);
    userRepository.save(user);

    return Message.INITIALIZE_PORTFOLIO_SUCCESS.getMessage();
  }

  // 사용자 ID로 포트폴리오 조회
  public PortfolioDto getUserPortfolio(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    List<PortfolioEntity> portfolios = portfolioRepository.findByUser(user);

    List<PortfolioDto.CryptoInfo> cryptoInfoList = portfolios.stream()
        .map(portfolio -> {
          BigDecimal currentPrice = BigDecimal.valueOf(cryptocurrencyService.fetchCurrentPrice(portfolio.getCryptoSymbol()));
          String profitPercentage = calculateProfitPercentage(
              BigDecimal.valueOf(portfolio.getAvgPrice()), currentPrice);

          return new PortfolioDto.CryptoInfo(
              portfolio.getCryptoSymbol(),
              BigDecimal.valueOf(portfolio.getAmount()),
              BigDecimal.valueOf(portfolio.getAvgPrice()),
              currentPrice,
              profitPercentage
          );
        })
        .collect(Collectors.toList());

    return new PortfolioDto(
        user.getUsername(),
        BigDecimal.valueOf(user.getBalance()),
        BigDecimal.valueOf(user.getDebt()),
        LocalDateTime.now(),
        cryptoInfoList
    );
  }

  // 사용자의 모든 가상화폐 보유 내역을 삭제
  public void clearPortfolio(UserEntity user) {
    portfolioRepository.deleteByUser(user);
  }

  // 수익률 계산
  public String calculateProfitPercentage(BigDecimal avgPrice, BigDecimal currentPrice) {
    if (avgPrice.compareTo(BigDecimal.ZERO) == 0) return "0.00";

    BigDecimal profit = currentPrice.subtract(avgPrice)
        .divide(avgPrice, 4, BigDecimal.ROUND_HALF_UP)
        .multiply(BigDecimal.valueOf(100));

    return new DecimalFormat("+0.00;-0.00").format(profit) + "%";
  }
}



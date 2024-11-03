package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.TickerDTO;
import com.baeksh.cryptoSimulator.entity.PortfolioEntity;
import com.baeksh.cryptoSimulator.entity.TransactionEntity;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.repository.PortfolioRepository;
import com.baeksh.cryptoSimulator.repository.TransactionRepository;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.dto.PortfolioDto;
import java.text.DecimalFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PortfolioService {
  
  private final PortfolioRepository portfolioRepository;
  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;
  private final CryptocurrencyService cryptocurrencyService;

  // 초기 시드머니 제공 및 포트폴리오 생성
  @Transactional
  public void initializePortfolio(UserEntity user) {
    user.setBalance(1000000.0); // 초기 시드머니 100만 원
    userRepository.save(user); 
  }

  // 투자 금액, 수익률 계산
  public double calculateTotalValue(UserEntity user) {
    List<PortfolioEntity> portfolios = portfolioRepository.findByUser(user);
    return portfolios.stream()
        .mapToDouble(p -> p.getAmount() * p.getAvgPrice())
        .sum();
    }

  // 빚 여부 확인 및 패널티 적용
  public void applyDebtPenalty(UserEntity user) {
    if (user.getBalance() < 0) {
      user.setDebt(user.getDebt() + Math.abs(user.getBalance())); // 빚 증가
      userRepository.save(user);
      
      /*
       * TO-DO
       * 옵션 기능 구현할때 빚 패널티를 자세하게 설정
       * */
      } 
  }
  
  //사용자 ID로 포트폴리오 조회
  public PortfolioDto getUserPortfolio(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    List<PortfolioEntity> portfolios = portfolioRepository.findByUser(user);

    // 포트폴리오 데이터 생성
    List<PortfolioDto.CryptoInfo> cryptoInfoList = portfolios.stream()
        .map(portfolio -> {
            BigDecimal currentPrice = BigDecimal.valueOf(fetchCurrentPrice(portfolio.getCryptoSymbol()));
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
  
  //실시간 가격 가져오는 메서드
  private double fetchCurrentPrice(String cryptoSymbol) {
    List<TickerDTO> tickerData = cryptocurrencyService.fetchTickerData(cryptoSymbol);
    return tickerData.isEmpty() ? 0 : tickerData.get(0).getTradePrice(); // 현재 시세 반환    
  }
  
  
  //수익률 계산 (퍼센트)
  public String calculateProfitPercentage(BigDecimal avgPrice, BigDecimal currentPrice) {
    if (avgPrice.compareTo(BigDecimal.ZERO) == 0) return "0.00";
    
    // 수익률은 (현재 가격 - 평균 매수가) / 평균 매수가 * 100 -> 
    BigDecimal profit = currentPrice.subtract(avgPrice)
        .divide(avgPrice, 4, BigDecimal.ROUND_HALF_UP)
        .multiply(BigDecimal.valueOf(100));

    // 양수/음수 포맷팅
    DecimalFormat df = new DecimalFormat("+0.00;-0.00");
    return df.format(profit) + "%";
    
  }
  
  //사용자의 모든 가상화폐 보유 내역을 삭제
  public void clearPortfolio(UserEntity user) {
    portfolioRepository.deleteByUser(user);
    
  }
  
  // 포트폴리오에 새 거래 반영
  public void updatePortfolio(UserEntity user, String cryptoSymbol, double amount, String transactionType) {
    double currentPrice = fetchCurrentPrice(cryptoSymbol); // 실시간 시세 정보 가져오기
    
    double totalCost = amount * currentPrice;

    // 매수 시 잔액 검증
    if (transactionType.equals("BUY") && user.getBalance() < totalCost) {
        throw new CustomException(ErrorCode.INSUFFICIENT_FUNDS); // 잔액 부족 예외 처리
    }
    
    PortfolioEntity portfolio = portfolioRepository.findByUserAndCryptoSymbol(user, cryptoSymbol)
        .orElse(PortfolioEntity.builder()
            .user(user)
            .cryptoSymbol(cryptoSymbol)
            .amount(0)
            .avgPrice(0)
            .build());

    if (transactionType.equals("BUY")) {
      double newTotalCost = (portfolio.getAmount() * portfolio.getAvgPrice()) + totalCost;
      portfolio.setAmount(portfolio.getAmount() + amount);
      portfolio.setAvgPrice(totalCost / portfolio.getAmount());
      user.setBalance(user.getBalance() - (amount * currentPrice)); // 잔액 감소
      } else {
        portfolio.setAmount(portfolio.getAmount() - amount);
        user.setBalance(user.getBalance() + (amount * currentPrice)); // 잔액 증가
      }

    portfolioRepository.save(portfolio);
    transactionRepository.save(TransactionEntity.builder()
        .user(user)
        .cryptoSymbol(cryptoSymbol)
        .transactionType(transactionType)
        .amount(amount)
        .price(currentPrice) // 실시간 가격으로 거래
        .build());
    
    applyDebtPenalty(user);
    
  }
  
}

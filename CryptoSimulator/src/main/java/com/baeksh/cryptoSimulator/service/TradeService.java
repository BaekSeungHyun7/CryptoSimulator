package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.TradeRequestDto;
import com.baeksh.cryptoSimulator.entity.PortfolioEntity;
import com.baeksh.cryptoSimulator.entity.TransactionEntity;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.entity.TransactionType;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.message.Message;
import com.baeksh.cryptoSimulator.repository.PortfolioRepository;
import com.baeksh.cryptoSimulator.repository.TransactionRepository;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

  private final PortfolioRepository portfolioRepository;
  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;
  private final CryptocurrencyService cryptocurrencyService;

  // 매수/매도 로직
  @Transactional
  public synchronized void executeTrade(Long userId, TradeRequestDto tradeRequest) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_FOR_TRADE));

    updatePortfolio(user, tradeRequest.getCryptoSymbol(), tradeRequest.getAmount(), tradeRequest.getTransactionType());
  }

  // 포트폴리오에 거래 내역 갱신
  public void updatePortfolio(UserEntity user, String cryptoSymbol, double amount, TransactionType transactionType) {
    double currentPrice = cryptocurrencyService.fetchCurrentPrice(cryptoSymbol);
    double totalCost = amount * currentPrice;

    if (transactionType == TransactionType.BUY && user.getBalance() < totalCost) {
      throw new CustomException(ErrorCode.INSUFFICIENT_FUNDS);
    }

    PortfolioEntity portfolio = portfolioRepository.findByUserAndCryptoSymbol(user, cryptoSymbol)
        .orElseGet(() -> PortfolioEntity.defaultPortfolio(user, cryptoSymbol));

    if (transactionType == TransactionType.BUY) { // 매수
      double newTotalCost = (portfolio.getAmount() * portfolio.getAvgPrice()) + totalCost;
      portfolio.setAmount(portfolio.getAmount() + amount);
      portfolio.setAvgPrice(newTotalCost / portfolio.getAmount());
      user.setBalance(user.getBalance() - totalCost);
    } else if (transactionType == TransactionType.SELL) { // 매도
      if (portfolio.getAmount() < amount) {
        throw new CustomException(ErrorCode.INSUFFICIENT_HOLDINGS);
      }
      portfolio.setAmount(portfolio.getAmount() - amount);
      user.setBalance(user.getBalance() + totalCost);
    }

    // 포트폴리오 및 트랜잭션 저장
    portfolioRepository.save(portfolio);
    transactionRepository.save(TransactionEntity.builder()
        .user(user)
        .cryptoSymbol(cryptoSymbol)
        .transactionType(transactionType)
        .amount(amount)
        .price(currentPrice)
        .build());

    applyDebtPenalty(user);
  }

  // 빚 여부 확인 및 패널티 적용
  public void applyDebtPenalty(UserEntity user) {
    if (user.getBalance() < 0) {
      user.setDebt(user.getDebt() + Math.abs(user.getBalance()));
      userRepository.save(user);
    }
  }
}



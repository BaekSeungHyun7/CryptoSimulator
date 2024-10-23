package com.baeksh.cryptoSimulator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CryptocurrencyService {

    private final RestTemplate restTemplate;

    public CryptocurrencyService(RestTemplate restTemplate) {
      this.restTemplate = restTemplate;
    }
    
    
    /**
     * 가상화폐 목록을 가져와 필요한 순서와 데이터로 변환
     * @return 정렬된 가상화폐 목록 (List<Map<String, Object>>)
     */
    public List<Map<String, Object>> getAllCryptocurrencies() {
      String marketUrl = "https://api.upbit.com/v1/market/all";
      String tickerUrl = "https://api.upbit.com/v1/ticker?markets=";

      // 1. 모든 코인 정보 가져오기 (한국명 포함)
      List<Map<String, Object>> markets = restTemplate.getForObject(marketUrl, List.class);

      // 2. 필요한 코인만 필터링 (KRW 시장)
      List<Map<String, Object>> krwMarkets = markets.stream()
              .filter(m -> m.get("market").toString().startsWith("KRW-"))
              .collect(Collectors.toList());

      // 3. KRW 마켓 심볼 추출
      String marketSymbols = krwMarkets.stream()
              .map(m -> m.get("market").toString())
              .collect(Collectors.joining(","));

      // 4. 모든 코인의 상세 정보를 한 번의 호출로 가져오기
      // 호출 제한용, 409
      List<Map<String, Object>> tickerData = restTemplate.getForObject(tickerUrl + marketSymbols, List.class);

      // 5. 결과 리스트 구성
      List<Map<String, Object>> result = new ArrayList<>();
      int rank = 1;

      for (Map<String, Object> ticker : tickerData) {
          String marketSymbol = ticker.get("market").toString(); //KRW-BTC
          String symbol = marketSymbol.split("-")[1]; //BTC

          // 해당 마켓의 한국명 찾기
          String koreanName = krwMarkets.stream()
                  .filter(m -> m.get("market").equals(marketSymbol))
                  .map(m -> m.get("korean_name").toString())
                  .findFirst()
                  .orElse("N/A");

          // 6. 필요한 데이터 추출 및 정리
          Map<String, Object> cryptoInfo = new LinkedHashMap<>();
          cryptoInfo.put("rank", rank++);
          cryptoInfo.put("name_korean", koreanName);
          cryptoInfo.put("symbol", symbol);
          cryptoInfo.put("price_krw", ticker.get("trade_price"));
          cryptoInfo.put("market_cap", ticker.get("acc_trade_price"));
          cryptoInfo.put("volume_24h", ticker.get("acc_trade_volume_24h"));
          cryptoInfo.put("change_24h", ticker.get("signed_change_rate"));
          
          result.add(cryptoInfo);
      }

      return result;
  }
}


/* 호출 데이터 목록
 * https://api.upbit.com/v1/market/all
 * https://api.upbit.com/v1/ticker?markets=KRW-BTC
 * 
"market": 거래 시장 정보 (KRW-BTC는 원화로 비트코인 거래)
"trade_date": 거래 날짜(UTC 기준)
"trade_time":거래 시간(UTC 기준)
"trade_date_kst":거래 날짜(KST 기준)
"trade_time_kst":거래 시간(KST 기준)
"trade_timestamp":거래가 발생한 시각의 타임스탬프 (밀리초 단위)
"opening_price":오늘의 시가
"high_price":오늘의 최고가
"low_price":오늘의 최저가
"trade_price":마지막 거래 가격 (현재가)
"prev_closing_price":전일 종가
"change":가격 변동 상태
"change_price":전일 대비 가격 변동 폭
"change_rate":전일 대비 변동률
"signed_change_price":부호 포함 가격 변동 폭
"signed_change_rate":부호 포함 변동률
"trade_volume":마지막 거래량
"acc_trade_price":누적 거래 금액
"acc_trade_price_24h":최근 24시간 누적 거래 금액
"acc_trade_volume":누적 거래량
"acc_trade_volume_24h":최근 24시간 누적 거래량
"highest_52_week_price":최근 52주 동안의 최고가
"highest_52_week_date":52주 최고가가 기록된 날짜
"lowest_52_week_price":최근 52주 동안의 최저가
"lowest_52_week_date":52주 최저가가 기록된 날짜
"timestamp": 데이터 생성 시각의 타임스탬프 (밀리초 단위)
 
 * */


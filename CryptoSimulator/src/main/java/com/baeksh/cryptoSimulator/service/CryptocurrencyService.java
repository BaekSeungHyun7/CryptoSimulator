package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.MarketDTO;
import com.baeksh.cryptoSimulator.dto.TickerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptocurrencyService {

    //RestTemplate 인스턴스
    private final RestTemplate restTemplate;

    /**
     * 가상화폐 목록을 가져오고 TickerDTO 객체로 변환하여 반환합니다.
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return TickerDTO 객체의 리스트
     */
    public List<TickerDTO> getAllCryptocurrencies(int page, int size) {
        // 시장 데이터
        List<MarketDTO> markets = fetchMarkets();
        // KRW 마켓필터링
        List<MarketDTO> krwMarkets = filterKRWMarkets(markets);
        // 심볼
        String marketSymbols = krwMarkets.stream()
                .map(MarketDTO::getMarket)
                .collect(Collectors.joining(","));
        //시세정보
        List<TickerDTO> tickerData = fetchTickerData(marketSymbols);

        // MarketDTO의 한국어명과 심볼을 TickerDTO에 매핑
        Map<String, String> koreanNameMap = krwMarkets.stream()
                .collect(Collectors.toMap(MarketDTO::getMarket, MarketDTO::getKorean_name));

        // TickerDTO 객체에 심볼과 한국어 이름 설정
        return tickerData.stream()
                .peek(ticker -> {
                    String[] parts = ticker.getMarket().split("-");
                    ticker.setSymbol(parts[1]);  // 예: KRW-BTC은 비트코인 BTC
                    ticker.setKoreanName(koreanNameMap.get(ticker.getMarket()));  // 한국어명 매핑
                })
                .collect(Collectors.toList());
    }

    /**
     * 모든 시장 데이터를 가져오는 메서드
     *
     * @return MarketDTO 객체의 리스트
     */
    private List<MarketDTO> fetchMarkets() {
        ResponseEntity<List<MarketDTO>> response = restTemplate.exchange(
            "https://api.upbit.com/v1/market/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MarketDTO>>() {}
        );
        return response.getBody();
    }
    
    /**
     * KRW 마켓만 필터링
     *
     * @param markets 전체 시장 목록
     * @return KRW 마켓만 포함된 MarketDTO 객체의 리스트
     */
    private List<MarketDTO> filterKRWMarkets(List<MarketDTO> markets) {
        return markets.stream()
                .filter(m -> m.getMarket().startsWith("KRW-")) //KRW 필터링
                .collect(Collectors.toList());
    }

    /**
     * 주어진 심볼에 대한 시세 정보
     *
     * @param marketSymbols 시장 심볼 리스트
     * @return TickerDTO 객체의 리스트
     */
    private List<TickerDTO> fetchTickerData(String marketSymbols) {
        ResponseEntity<List<TickerDTO>> response = restTemplate.exchange(
            "https://api.upbit.com/v1/ticker?markets=" + marketSymbols,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<TickerDTO>>() {}
        );
        return response.getBody(); //API 반환
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


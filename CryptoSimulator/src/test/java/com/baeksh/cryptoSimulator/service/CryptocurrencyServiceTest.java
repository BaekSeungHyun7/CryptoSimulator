package com.baeksh.cryptoSimulator.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

class CryptocurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;  // RestTemplate 모의 객체

    @InjectMocks
    private CryptocurrencyService cryptocurrencyService;  // 테스트 대상 서비스

    private final String marketResponse =
    """
        [
            {"market": "KRW-BTC", "korean_name": "비트코인", "english_name": "Bitcoin"},
            {"market": "KRW-ETH", "korean_name": "이더리움", "english_name": "Ethereum"}
        ]
    """
        ;

    private final String tickerResponse =
    """
        [
            {
                "market": "KRW-BTC",
                "trade_price": 49767555.8,
                "acc_trade_volume_24h": 507877.0,
                "acc_trade_price": 943060000000,
                "signed_change_rate": 0.0938
            },
            {
                "market": "KRW-ETH",
                "trade_price": 3618623.3,
                "acc_trade_volume_24h": 123456.0,
                "acc_trade_price": 432467000000,
                "signed_change_rate": -0.0212
            }
        ]
    """
    ;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        restTemplate = mock(RestTemplate.class);
        cryptocurrencyService = new CryptocurrencyService(restTemplate);
    }

    @Test
    void getAllCryptocurrencies_shouldReturnValidList() {
        // Mock 응답 설정
        when(restTemplate.getForObject("https://api.upbit.com/v1/market/all", String.class))
                .thenReturn(marketResponse);

        when(restTemplate.getForObject(
                eq("https://api.upbit.com/v1/ticker?markets=KRW-BTC,KRW-ETH"), 
                eq(String.class))
        ).thenReturn(tickerResponse);

        // 서비스 메서드 호출
        List<Map<String, Object>> result = cryptocurrencyService.getAllCryptocurrencies();

        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // 첫 번째 항목 검증
        Map<String, Object> btc = result.get(0);
        assertThat(btc.get("name")).isEqualTo("BTC");
        assertThat(btc.get("price")).isEqualTo(49767555.8);
        assertThat(btc.get("volume_24h")).isEqualTo(507877.0);
        assertThat(btc.get("market_cap")).isEqualTo(943060000000.0);
        assertThat(btc.get("change_24h")).isEqualTo(9.38);

        // 두 번째 항목 검증
        Map<String, Object> eth = result.get(1);
        assertThat(eth.get("name")).isEqualTo("ETH");
        assertThat(eth.get("price")).isEqualTo(3618623.3);
        assertThat(eth.get("volume_24h")).isEqualTo(123456.0);
        assertThat(eth.get("market_cap")).isEqualTo(432467000000.0);
        assertThat(eth.get("change_24h")).isEqualTo(-2.12);
    }
}

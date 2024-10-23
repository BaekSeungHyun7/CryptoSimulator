package com.baeksh.cryptoSimulator.controller;

import com.baeksh.cryptoSimulator.service.CryptocurrencyService; 
import org.springframework.beans.factory.annotation.Autowired;      
import org.springframework.web.bind.annotation.GetMapping;       
import org.springframework.web.bind.annotation.RequestMapping;   
import org.springframework.web.bind.annotation.RestController;

import java.util.List;                                             
import java.util.Map;                                              

/**
 * 가상화폐 정보를 제공하는 API의 엔드포인트
 * CryptocurrencyService 데이터 처리하고 반환하는 역할
 */

@RestController
@RequestMapping("/api/cryptocurrencies")
public class CryptocurrencyController {

    private final CryptocurrencyService cryptoService; // 가상화폐 서비스 인스턴스

    /**
     * CryptocurrencyService를 주입받고
     * @param cryptoService 가상화폐 정보를 처리
     */
    @Autowired
    public CryptocurrencyController(CryptocurrencyService cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * 가상화폐 목록을 반환하는 API 엔드포인트
     * HTTP GET 요청을 "/list" 경로에 매핑하며, 서비스에서 가져온 가상화폐 데이터를 반환
     * @return 가상화폐 정보 목록 (List<Map<String, Object>>)
     */
    @GetMapping("/list")
    public List<Map<String, Object>> getAllCryptocurrencies() {
        // CryptocurrencyService를 호출해 모든 가상화폐 데이터를 가져와 반환
        return cryptoService.getAllCryptocurrencies();
    }
   
}


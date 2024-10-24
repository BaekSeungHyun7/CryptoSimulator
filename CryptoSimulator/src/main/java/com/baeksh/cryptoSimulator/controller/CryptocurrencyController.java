package com.baeksh.cryptoSimulator.controller;

import com.baeksh.cryptoSimulator.dto.TickerDTO;
import com.baeksh.cryptoSimulator.service.CryptocurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cryptocurrencies")
@RequiredArgsConstructor
public class CryptocurrencyController {

    /**
     * 가상화폐 목록을 가져오는 GET API 엔드포인트입니다.
     *
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @return TickerDTO 객체의 리스트
     */
    private final CryptocurrencyService cryptoService;

    @GetMapping("/list")
    public List<TickerDTO> getAllCryptocurrencies(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return cryptoService.getAllCryptocurrencies(page, size);
    }
}






package com.baeksh.cryptoSimulator.frtest;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Frtest {

    @PostConstruct
    public void printMessage() {
        System.out.println("테스트테스트머지를바로하면머지머지");
    }
}

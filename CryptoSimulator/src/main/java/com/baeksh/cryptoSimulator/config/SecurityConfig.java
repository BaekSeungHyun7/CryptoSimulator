package com.baeksh.cryptoSimulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                //html 허용
                .requestMatchers("/", "/index.html", "/static/**", "/js/**", "/css/**").permitAll()
                // 모든 요청에 대해 인증 없이 접근 허용
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())  // 임시용 CSRF 비활성화
            .headers(headers -> headers.frameOptions().disable()) // H2 콘솔용
            .formLogin().disable()  // 로그인 폼 비활성화
            .httpBasic().disable();  // HTTP Basic 인증 비활성화

        return http.build();
    }
}




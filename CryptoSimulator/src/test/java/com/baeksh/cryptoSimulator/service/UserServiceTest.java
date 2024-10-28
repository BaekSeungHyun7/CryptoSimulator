package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.UserDto;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import com.baeksh.cryptoSimulator.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {
  
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        // Given
        UserDto.SignUp signUpRequest = new UserDto.SignUp();
        signUpRequest.setUsername("test");
        signUpRequest.setPassword("241027");
        signUpRequest.setEmail("test1@example.com");
        signUpRequest.setPhone("01012345678");
        signUpRequest.setRole("USER"); //ADMIN //USER

        when(userRepository.findByUsername(signUpRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserEntity result = userService.register(signUpRequest);

        // Then
        assertNotNull(result);
        logger.info("유저 가입 성공: {}", result);
    }

    @Test
    void testLogin() {
        logger.info("로그인 테스트 시작");
        // Given
        UserDto.Login loginRequest = new UserDto.Login();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        UserEntity userEntity = UserEntity.builder()
                .username("testUser")
                .password("encodedPassword")
                .role("USER")
                .build();

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(jwtTokenProvider.createToken(userEntity.getUsername(), userEntity.getRole())).thenReturn("jwtToken");

        // When
        String token = userService.login(loginRequest);

        // Then
        assertNotNull(token);
        logger.info("유저 로그인 성공 : ", token);
        assertEquals("jwtToken", token);
    }
}
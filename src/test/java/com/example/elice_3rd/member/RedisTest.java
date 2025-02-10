package com.example.elice_3rd.member;

import com.example.elice_3rd.security.jwt.entity.RefreshToken;
import com.example.elice_3rd.security.jwt.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {
    private static final Logger log = LoggerFactory.getLogger(RedisTest.class);
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void test(){
        RefreshToken refreshToken = RefreshToken.builder()
                .email("test_email")
                .refreshToken("test token")
                .expiration("test expiration")
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("size = {}", refreshTokenRepository.findAll().size());

        for(RefreshToken token : refreshTokenRepository.findAll()){
            if(token == null)
                continue;
            log.info("id : {} \n", token.getId());
            log.info("email: {} \n", token.getEmail());
        }

        log.info("count : {}", refreshTokenRepository.findByEmail("test_email").orElseThrow().getRefreshToken());
    }
}

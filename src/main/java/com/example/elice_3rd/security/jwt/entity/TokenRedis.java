package com.example.elice_3rd.security.jwt.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Indexed;

@Getter
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7)
@AllArgsConstructor
public class TokenRedis {
    @Id
    private String id;
//    @Indexed
    private String accessToken;
    private String refreshToken;
}

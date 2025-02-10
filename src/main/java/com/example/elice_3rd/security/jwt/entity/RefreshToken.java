package com.example.elice_3rd.security.jwt.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "token", timeToLive = 604800000)
@Builder
public class RefreshToken {
    @Id
    private String id;
    private String email;
    private String refreshToken;
    private String expiration;
}

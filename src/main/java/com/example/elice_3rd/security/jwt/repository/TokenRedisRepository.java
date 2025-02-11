package com.example.elice_3rd.security.jwt.repository;

import com.example.elice_3rd.security.jwt.entity.TokenRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRedisRepository extends CrudRepository<TokenRedis, String> {
    Optional<TokenRedis> findByAccessToken(String accessToken);
}

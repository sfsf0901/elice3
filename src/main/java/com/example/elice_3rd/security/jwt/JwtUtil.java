package com.example.elice_3rd.security.jwt;

import com.example.elice_3rd.security.jwt.entity.RefreshToken;
import com.example.elice_3rd.security.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final RefreshTokenRepository tokenRepository;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTime;
    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    public JwtUtil(@Value("${jwt.token.secret}") String secret, RefreshTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String createAccessToken(String email, String role) {
        Claims claims = Jwts.claims();
        claims.put("category", "access");
        claims.put("email", email);
        claims.put("role", role);
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email, String role) {
        Claims claims = Jwts.claims();
        claims.put("category", "refresh");
        claims.put("email", email);
        claims.put("role", role);
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

//        redisTemplate.opsForValue().set(
//                authentication.getName(),
//                refreshToken,
//                refreshExpirationTime,
//                TimeUnit.MILLISECONDS
//        );

        return refreshToken;
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String getCategory(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("category", String.class);
    }

    public Boolean isExist(String token) {
        return tokenRepository.existsById(getEmail(token));
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        log.warn("access token 삭제! email = {}", getEmail(token));
        tokenRepository.deleteById(getEmail(token));
    }

    public void addRefreshToken(String email, String refreshToken) {
        tokenRepository.save(RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationTime).toString())
                .build());
    }

    public String getRefreshToken(String accessToken){
        RefreshToken refreshToken = tokenRepository.findByEmail(getEmail(accessToken)).orElseThrow(
                () -> new IllegalArgumentException("refresh token does not exist")
        );
        return refreshToken.getRefreshToken();
    }
}

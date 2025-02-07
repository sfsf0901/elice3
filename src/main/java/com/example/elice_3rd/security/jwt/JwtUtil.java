package com.example.elice_3rd.security.jwt;

import com.example.elice_3rd.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final Key key;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTime;
    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    public JwtUtil(@Value("${jwt.token.secret}") String secret) {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String createAccessToken(String email, String role, Authentication authentication){
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("email", email);
        claims.put("role", role);
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email, String role, Authentication authentication){
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("email", email);
        claims.put("role", role);
        Date now = new Date();
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

    public String getEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public String getRole(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public Boolean isExpired(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String getCategory(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("category", String.class);
    }

    public String createJwt(String category, String email, String role, Long expiredMs){
        Claims claims = Jwts.claims();
        claims.put("category", category);
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

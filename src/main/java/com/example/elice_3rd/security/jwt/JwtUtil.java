package com.example.elice_3rd.security.jwt;

import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.security.jwt.entity.RefreshToken;
import com.example.elice_3rd.security.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("email", String.class);
        } catch (ExpiredJwtException e){
            // 만료가 되었어도 이메일 정보는 반환
            return e.getClaims().get("email", String.class);
        }

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

    public String getRefreshToken(String accessToken) {
        // 예외 메시지 더욱 상세하게 코드를 모르는 사람도 메시지를 보고 알아볼 수 있을 정도로
        RefreshToken refreshToken = tokenRepository.findByEmail(getEmail(accessToken)).orElseThrow(
                () -> new NoSuchDataException("getRefreshToken failed: 토큰 정보와 일치하는 refresh token이 존재하지 않습니다.")
        );
        return refreshToken.getRefreshToken();
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) refreshExpirationTime / 1000);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies)
                if (cookie.getName().equals("access")) {
                    accessToken = cookie.getValue();
                    break;
                }
        }

        String refreshToken = null;
        try {
            refreshToken = getRefreshToken(accessToken);
        } catch (RuntimeException e){
            throw new InsufficientAuthenticationException("Refresh token does not exist");
        }

        if(refreshToken == null)
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);

        try {
            isExpired(refreshToken);
        } catch (ExpiredJwtException e){
            return new ResponseEntity<>("refresh token is expired", HttpStatus.BAD_REQUEST);
        }

        String category = getCategory(refreshToken);

        if(!category.equals("refresh"))
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        String email = getEmail(refreshToken);
        String role = getRole(refreshToken);

        String newAccessToken = createAccessToken(email, role);
        String newRefreshToken = createRefreshToken(email, role);

        addRefreshToken(email, newRefreshToken);
        response.addCookie(createCookie("access", newAccessToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

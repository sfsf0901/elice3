package com.example.elice_3rd.security.jwt.controller;

import com.example.elice_3rd.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@AllArgsConstructor
public class ReissueController {
    private final JwtUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies)
            if(cookie.getName().equals("refresh"))
                refreshToken = cookie.getValue();

        if(refreshToken == null)
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e){
            return new ResponseEntity<>("refresh token is expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refreshToken);

        if(!category.equals("refresh"))
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(email, role);
        String newRefreshToken = jwtUtil.createRefreshToken(email, role);

        // 기존의 refresh token 삭제 후 새로운 토큰 추가
//        jwtUtil.deleteRefreshToken(email);
        jwtUtil.addRefreshToken(email, newRefreshToken);

        response.setHeader("access", newAccessToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

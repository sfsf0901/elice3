package com.example.elice_3rd.member.oauth2;

import com.example.elice_3rd.security.CustomUserDetails;
import com.example.elice_3rd.security.MemberDetail;
import com.example.elice_3rd.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        Map<String, Object> userInfo = oauth2User.getAttributes();
        if(userInfo.get("kakao_account") != null)
            userInfo = (Map<String, Object>) userInfo.get("kakao_account");
        else if(userInfo.get("response") != null)
            userInfo = (Map<String, Object>) userInfo.get("response");

        ObjectMapper objectMapper = new ObjectMapper();

        log.warn("OAUTH 로그인 성공 메서드 호출!");


        String email = userInfo.get("email").toString();
        String role = "USER";

        log.warn(objectMapper.writeValueAsString(oauth2User) + "\n");
        log.warn(objectMapper.writeValueAsString(userInfo));
        log.warn("email = {}", email);
        log.warn("role = {}", role);

        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email, role);

        jwtUtil.addRefreshToken(email, refreshToken);

        log.warn("accessToken = {}", accessToken);

        response.addCookie(jwtUtil.createCookie("access", accessToken));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("/");
    }
}

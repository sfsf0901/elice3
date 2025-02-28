package com.example.elice_3rd.security.oauth2;

import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        Map<String, Object> userInfo = oauth2User.getAttributes();
        if(userInfo.get("kakao_account") != null)
            userInfo = (Map<String, Object>) userInfo.get("kakao_account");
        else if(userInfo.get("response") != null)
            userInfo = (Map<String, Object>) userInfo.get("response");

        String email = "";
        if(userInfo.get("email") != null)
            email = userInfo.get("email").toString();

        Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                new NoSuchDataException("회원 조회 실패: 이메일과 일치하는 회원이 존재하지 않습니다.")
        );

        String role = member.getRole().getKey();
        String name = member.getName();
        Boolean isOauth = member.getIsOauth();

        String accessToken = jwtUtil.createAccessToken(email, role, name, isOauth);
        String refreshToken = jwtUtil.createRefreshToken(email, role, name, isOauth);

        jwtUtil.addRefreshToken(email, refreshToken);

        response.addCookie(jwtUtil.createCookie("access", accessToken));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("/");
    }
}

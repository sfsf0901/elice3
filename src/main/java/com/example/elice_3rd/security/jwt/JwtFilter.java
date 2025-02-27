package com.example.elice_3rd.security.jwt;

import com.example.elice_3rd.security.CustomUserDetails;
import com.example.elice_3rd.security.MemberDetail;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        log.warn(request.getRequestURI());
        log.info("JWTFilter called");

        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals("access")){
                    accessToken = cookie.getValue();
                    break;
                }
            }
            if(accessToken == null) {
                log.error("token is null");
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            log.error("token is null");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e){
            // 토큰 재발급
            try {
                jwtUtil.reissue(request, response);
            } catch (RuntimeException e1){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if(!category.equals("access")){
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);
        String name = jwtUtil.getName(accessToken);

        // TODO memberRepository 안쓰기
        MemberDetail memberDetail = MemberDetail.builder()
                .email(email)
                .role(role)
                .name(name)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(memberDetail);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.endsWith(".js") || path.endsWith(".css") || path.startsWith("/images") || path.endsWith(".map");
    }
}

package com.example.elice_3rd.security.jwt;

import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.security.CustomUserDetails;
import com.example.elice_3rd.security.MemberDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer")){
            log.error("token is null");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if(jwtUtil.isExpired(token)){
            log.error("token is expired");
            filterChain.doFilter(request, response);

            return;
        }

        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        MemberDetail memberDetail = MemberDetail.builder()
                .email(email)
                .password("temppassword")
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(memberDetail);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

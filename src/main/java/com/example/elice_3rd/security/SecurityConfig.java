package com.example.elice_3rd.security;

import com.example.elice_3rd.member.oauth2.CustomOAuth2UserService;
import com.example.elice_3rd.security.jwt.JwtFilter;
import com.example.elice_3rd.security.jwt.JwtUtil;
import com.example.elice_3rd.security.jwt.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/doctor").hasRole("DOCTOR")
                    .requestMatchers("/**").permitAll()
                    .anyRequest().authenticated();
        });

        http.logout(logout -> {
            logout.logoutUrl("/logout")
//                    .logoutSuccessHandler(logout.getLogoutSuccessHandler())
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .permitAll();
        });

        http.oauth2Login(oauth2 -> {
            oauth2.userInfoEndpoint(userInfoEndpointConfig -> {
                userInfoEndpointConfig.userService(customOAuth2UserService);
            });
        });

        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService();
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(){
//        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
//        OAuth2AccessTokenResponseHttpMessageConverter converter = new OAuth2AccessTokenResponseHttpMessageConverter();
//
//
////        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));
//
//        RestTemplate restTemplate = new RestTemplate();
//        List<HttpMessageConverter<?>> converters = new ArrayList<>();
//        converters.add(new FormHttpMessageConverter());
//        converters.add(new JsonbHttpMessageConverter());
//        restTemplate.setMessageConverters(converters);
//
//
//        tokenResponseClient.setRestOperations(new RestTemplate(List.of(converter)));
//        return tokenResponseClient;
//    }
}

package com.example.elice_3rd.security.oauth2;

import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import com.example.elice_3rd.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeNAme = userRequest.getClientRegistration().
                getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeNAme, oAuth2User.getAttributes());
        Member member = saveOrUpdate(attributes);

        MemberResponseDto memberDto = member.toResponseDto();

        try {
            log.warn(objectMapper.writeValueAsString(attributes) );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(memberDto.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Member saveOrUpdate(OAuth2Attributes attributes){
        MemberUpdateDto updateDto = MemberUpdateDto.builder()
                .name(attributes.getName())
                .contact(attributes.getContact())
                .build();

        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.updateInfo(updateDto))
                .orElse(attributes.toEntity());

        return memberRepository.save(member);
    }
}

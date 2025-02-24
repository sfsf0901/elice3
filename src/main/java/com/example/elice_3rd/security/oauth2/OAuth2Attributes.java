package com.example.elice_3rd.security.oauth2;

import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Builder
@Getter
public class OAuth2Attributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private Role role;
    private String provider;
    private String providerId;

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        if(registrationId.equals("naver"))
            return ofNaver(registrationId, userNameAttributeName, attributes);
        else if(registrationId.equals("kakao"))
            return ofKakao(registrationId, userNameAttributeName, attributes);
        else
            return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    public static OAuth2Attributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        return OAuth2Attributes.builder()
                .attributes(attributes)
                .name(attributes.get("name").toString())
                .email(attributes.get("email").toString())
                .provider(registrationId)
                .providerId(attributes.get("sub").toString())
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuth2Attributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .attributes(attributes)
                .name(response.get("name").toString())
                .email(response.get("email").toString())
                .provider(registrationId)
                .providerId(response.get("id").toString())
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuth2Attributes ofKakao(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");


        return OAuth2Attributes.builder()
                .attributes(attributes)
                .name(kakaoAccount.get("name").toString())
                .email(kakaoAccount.get("email").toString())
                .provider(registrationId)
                .providerId(attributes.get("id").toString())
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .role(Role.USER)
                .password(UUID.randomUUID().toString())
                .build();
    }
}

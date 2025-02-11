package com.example.elice_3rd.member.oauth2;

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
    private String contact;
    private Role role;
    private String provider;
    private String providerId;

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
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

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .contact("test contact")
                .role(Role.USER)
                .password(UUID.randomUUID().toString())
                .build();
    }
}

package com.example.elice_3rd.security;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

//TODO member 객체에 getter를 안쓸 수 있는 방법 모색
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final MemberDetail memberDetail;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return memberDetail.getRole();
            }
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return memberDetail.getPassword();
    }

    @Override
    public String getUsername() {
        return memberDetail.getEmail();
    }

    public Boolean isDeleted(){
        return memberDetail.getIsDeleted();
    }

    public Boolean isOauth(){
        return memberDetail.getIsOauth();
    }

    public String getName(){
        return memberDetail.getName();
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
}

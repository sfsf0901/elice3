package com.example.elice_3rd.security;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDetail {
    private String email;
    private String password;
    private String role;
}

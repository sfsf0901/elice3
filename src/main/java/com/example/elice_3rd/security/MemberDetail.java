package com.example.elice_3rd.security;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetail {
    private String email;
    private String password;
    private String role;
    private Boolean isDeleted;
}

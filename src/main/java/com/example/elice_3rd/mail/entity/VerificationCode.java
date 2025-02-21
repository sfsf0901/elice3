package com.example.elice_3rd.mail.entity;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "code",timeToLive = 300)
@Builder
public class VerificationCode {
    @Id
    private String code;
    private Boolean isVerify;

    public void verify(){
        isVerify = true;
    }
}

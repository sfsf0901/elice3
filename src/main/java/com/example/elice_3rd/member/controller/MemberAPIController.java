package com.example.elice_3rd.member.controller;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.service.MemberService;
import com.example.elice_3rd.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
public class MemberAPIController {
    private final MemberService memberService;
    // component를 주입 시키는 것에는 정답은 없음
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Validated MemberRequestDto requestDto) throws JsonProcessingException {
        try {
            memberService.register(requestDto);
            return ResponseEntity.created(URI.create("/login")).build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("info")
    public ResponseEntity<MemberResponseDto> retrieve(Principal principal){
        return ResponseEntity.ok(memberService.retrieveMember(principal.getName()));
    }

    @PatchMapping("password")
    public ResponseEntity<Void> updatePassword(Principal principal, @RequestBody String password){
        memberService.updatePassword(principal.getName(), password);
        return ResponseEntity.ok().header("Location", "my-page").build();
    }

    @PatchMapping("info")
    public ResponseEntity<Void> updateMemberInfo(Principal principal, @RequestBody MemberUpdateDto updateDto){
        memberService.updateMemberInfo(principal.getName(), updateDto);
        return ResponseEntity.ok().header("Location", "my-page").build();
    }

    @PatchMapping("quit")
    public ResponseEntity<Void> quit(Principal principal){
        memberService.quit(principal.getName());
        return ResponseEntity.ok().header("Location", "/").build();
    }

    @PatchMapping("role")
    public ResponseEntity<Void> changeRole(Principal principal){
        memberService.updateRole(principal.getName());
        return ResponseEntity.ok().header("Location", "/my-page").build();
    }

    @GetMapping("401")
    public ResponseEntity<?> test401(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("1234");
    }
}

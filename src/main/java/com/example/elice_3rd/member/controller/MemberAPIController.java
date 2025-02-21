package com.example.elice_3rd.member.controller;

import com.example.elice_3rd.comment.dto.CommentResponseDto;
import com.example.elice_3rd.comment.service.CommentService;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.service.CounselService;
import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.service.MemberService;
import com.example.elice_3rd.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
public class MemberAPIController {
    private final MemberService memberService;
    // component를 주입 시키는 것에는 정답은 없음
    private final CounselService counselService;
    private final CommentService commentService;

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
    public ResponseEntity<Void> quit(Principal principal, @RequestBody String password){
        memberService.quit(principal.getName());
        return ResponseEntity.ok().header("Location", "/").build();
    }

    @PatchMapping("role")
    public ResponseEntity<Void> changeRole(Principal principal){
        memberService.updateRole(principal.getName());
        return ResponseEntity.ok().header("Location", "/my-page").build();
    }

    @GetMapping("counsels")
    public ResponseEntity<Page<CounselResponseDto>> retrieveMyCounsels(Principal principal,
                                                                       @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(counselService.retrieveMyCounsels(principal.getName(), pageable));
    }

    @GetMapping("comments")
    public ResponseEntity<Page<CommentResponseDto>> retrieveMyComments(Principal principal,
                                                                       @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(commentService.retrieveMyComments(principal.getName(), pageable));
    }

    @PatchMapping("verify")
    public ResponseEntity<Void> verify(String code){
        memberService.verify(code);
        return ResponseEntity.ok().build();
    }
}
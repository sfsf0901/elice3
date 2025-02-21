package com.example.elice_3rd.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    // TODO : 테스트 완료 이후 헤더로 해당 버튼 기능 이전 필요
    @GetMapping("/test")
    public String getChatRoomBtnPage() {
        return "notification/notification";
    }
}

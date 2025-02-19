package com.example.elice_3rd.chat.controller;

import com.example.elice_3rd.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // TODO : 이후에 상담 상세 페이지로 해당 버튼 이전 필요
    @GetMapping("/check-chat-room")
    public String getChatRoomBtnPage() {
        return "chat/create-chat-room";
    }

    @GetMapping("/chat-room/{chatRoomId}/{memberId}")
    public String getChatRoom(@PathVariable Long chatRoomId, @PathVariable Long memberId, Model model) {
        if (!chatService.isChatRoomExist(chatRoomId)) {
            return "redirect:/";
        }
        return "chat/chat-room";
    }
}

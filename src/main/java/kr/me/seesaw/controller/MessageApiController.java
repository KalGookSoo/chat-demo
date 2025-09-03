package kr.me.seesaw.controller;

import kr.me.seesaw.dto.MessageResponse;
import kr.me.seesaw.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageApiController {
    private final MessageService messageService;

    @PreAuthorize("isAuthenticated() and @defaultMessageService.isMember(#chatRoomId, authentication.name)")
    @GetMapping
    public ResponseEntity<Map<String, Page<MessageResponse>>> getMessages(@RequestParam String chatRoomId) {
        Page<MessageResponse> messages = messageService.getMessagesByChatRoomId(chatRoomId);
        return ResponseEntity.ok(Map.of("messages", messages));
    }
}

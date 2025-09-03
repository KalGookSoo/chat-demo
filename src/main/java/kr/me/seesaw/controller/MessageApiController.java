package kr.me.seesaw.controller;

import kr.me.seesaw.dto.MessageResponse;
import kr.me.seesaw.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageApiController {
    private final MessageService messageService;

    @PreAuthorize("isAuthenticated() and @defaultMessageService.isMember(#chatRoomId, authentication.name)")
    @GetMapping
    public ResponseEntity<PagedModel<MessageResponse>> getMessages(@RequestParam String chatRoomId) {
        Page<MessageResponse> page = messageService.getMessagesByChatRoomId(chatRoomId);
        PagedModel<MessageResponse> pagedModel = new PagedModel<>(page);
        return ResponseEntity.ok(pagedModel);
    }
}

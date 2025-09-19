package kr.me.seesaw.controller;

import kr.me.seesaw.dto.ChatRoomResponse;
import kr.me.seesaw.security.PrincipalProvider;
import kr.me.seesaw.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;

    private final PrincipalProvider principalProvider;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, List<ChatRoomResponse>>> getChatRooms() {
        String userId = principalProvider.getAuthentication()
                .getDetails()
                .toString();
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(Map.of("chatRooms", chatRooms));
    }

}

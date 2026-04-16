package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.ChatRoomCreateRequest;
import kr.me.seesaw.domain.dto.ChatRoomResponse;
import kr.me.seesaw.domain.entity.ChatRoom;
import kr.me.seesaw.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "채팅방 API", description = "채팅방 조회 및 관리를 담당합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;

    private final PrincipalProvider principalProvider;

    @Operation(summary = "참여 중인 채팅방 목록 조회", description = "현재 로그인한 사용자가 참여 중인 채팅방 목록을 반환합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, List<ChatRoomResponse>>> getChatRooms() {
        String userId = principalProvider.getAuthentication()
                .getDetails()
                .toString();
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(Map.of("chatRooms", chatRooms));
    }

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성하고 친구들을 초대합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@Valid @RequestBody ChatRoomCreateRequest request) {
        String userId = principalProvider.getAuthentication()
                .getDetails()
                .toString();
        ChatRoom chatRoom = chatRoomService.createChatRoom(request.name(), userId, request.friendIds());
        return ResponseEntity.ok(new ChatRoomResponse(chatRoom.getId(), chatRoom.getName()));
    }

}

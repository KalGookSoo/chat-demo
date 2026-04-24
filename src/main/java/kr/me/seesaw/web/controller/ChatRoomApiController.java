package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.ChatRoomCreateRequest;
import kr.me.seesaw.domain.dto.ChatRoomMemberAddRequest;
import kr.me.seesaw.domain.dto.ChatRoomResponse;
import kr.me.seesaw.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms() {
        String userId = principalProvider.getAuthentication()
                .getDetails()
                .toString();

        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsByUserId(userId);

        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "채팅방 상세 조회", description = "특정 채팅방의 상세 정보와 참여자 목록을 조회합니다.")
    @PreAuthorize("isAuthenticated() and @chatRoomContext.isMember(#chatRoomId, authentication.details)")
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(@PathVariable String chatRoomId) {
        ChatRoomResponse chatRoom = chatRoomService.getChatRoom(chatRoomId);
        return ResponseEntity.ok(chatRoom);
    }

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성하고 친구들을 초대합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@Valid @RequestBody ChatRoomCreateRequest request) {
        String userId = principalProvider.getAuthentication()
                .getDetails()
                .toString();

        ChatRoomResponse chatRoom = chatRoomService.createChatRoom(request.name(), userId, request.friendIds());

        return ResponseEntity.ok(chatRoom);
    }

    @Operation(summary = "채팅방 멤버 추가", description = "채팅방에 새로운 멤버를 추가합니다.")
    @PreAuthorize("isAuthenticated() and @chatRoomContext.isMember(#chatRoomId, authentication.details)")
    @PostMapping("/{chatRoomId}/members")
    public ResponseEntity<Void> addMembers(@PathVariable String chatRoomId, @Valid @RequestBody ChatRoomMemberAddRequest request) {
        chatRoomService.addMembers(chatRoomId, request.memberIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 멤버 제거", description = "채팅방에서 멤버를 제거하거나 본인이 나갑니다.")
    @PreAuthorize("isAuthenticated() and @chatRoomContext.isMember(#chatRoomId, authentication.details)")
    @DeleteMapping("/{chatRoomId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable String chatRoomId, @PathVariable String memberId) {
        String requesterId = principalProvider.getAuthentication()
                .getDetails()
                .toString();
        chatRoomService.removeMember(chatRoomId, memberId, requesterId);
        return ResponseEntity.ok().build();
    }

}

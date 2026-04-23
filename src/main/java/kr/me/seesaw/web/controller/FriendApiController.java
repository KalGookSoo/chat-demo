package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.me.seesaw.domain.dto.FriendRequest;
import kr.me.seesaw.domain.dto.FriendResponse;
import kr.me.seesaw.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "친구 API", description = "친구 관리 및 상태 관리 API")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendApiController {

    private final FriendService friendService;

    @Operation(summary = "친구 요청", description = "상대방의 userId로 친구 요청을 보냅니다.")
    @PostMapping("/request")
    public ResponseEntity<Void> requestFriend(@RequestBody FriendRequest request) {
        friendService.requestFriend(request.username());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 수락", description = "나에게 온 친구 요청을 수락합니다.")
    @PutMapping("/{friendId}/accept")
    public ResponseEntity<Void> acceptFriend(@PathVariable String friendId) {
        friendService.acceptFriend(friendId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 삭제/거절", description = "친구 관계를 삭제하거나 요청을 거절합니다.")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable String friendId) {
        friendService.removeFriend(friendId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 목록 조회", description = "친구 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

}

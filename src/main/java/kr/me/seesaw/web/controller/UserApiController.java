package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.me.seesaw.domain.dto.PasswordChangeRequest;
import kr.me.seesaw.domain.dto.UserResponse;
import kr.me.seesaw.domain.dto.UserSearch;
import kr.me.seesaw.domain.dto.UserUpdateRequest;
import kr.me.seesaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자 API", description = "사용자 정보 조회 및 검색 API")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보 및 권한 목록을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserDetail(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Operation(summary = "사용자 검색", description = "친구 추가를 위해 사용자를 검색합니다. 계정명(username) 또는 이름(name)으로 검색 가능합니다.")
    @GetMapping
    public ResponseEntity<List<UserResponse>> searchUsers(UserSearch search) {
        return ResponseEntity.ok(userService.searchUsers(search));
    }

    @Operation(summary = "패스워드 변경", description = "계정의 패스워드를 변경합니다.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.details.toString()")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String userId, @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request.newPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 기본정보 수정", description = "계정 정보를 수정합니다.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.details.toString()")
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateProfile(@PathVariable String userId, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateProfile(userId, request.name());
        return ResponseEntity.ok().build();
    }

}

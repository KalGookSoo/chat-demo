package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.PushSubscriptionRequest;
import kr.me.seesaw.domain.dto.PushSubscriptionResponse;
import kr.me.seesaw.service.PushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "웹 푸시 구독 API", description = "브라우저별 웹 푸시 구독 정보를 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push/subscriptions")
public class PushSubscriptionApiController {

    private final PushSubscriptionService pushSubscriptionService;

    private final PrincipalProvider principalProvider;

    @Operation(summary = "푸시 구독 등록/갱신", description = "새로운 구독 정보를 등록하거나 기존 정보를 갱신합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PushSubscriptionResponse> subscribe(@Valid @RequestBody PushSubscriptionRequest request) {
        String userId = principalProvider.getAuthentication().getDetails().toString();
        PushSubscriptionResponse response = pushSubscriptionService.subscribe(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "푸시 구독 취소", description = "기존 구독 정보를 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestParam String endpoint) {
        pushSubscriptionService.unsubscribe(endpoint);
        return ResponseEntity.noContent().build();
    }

}

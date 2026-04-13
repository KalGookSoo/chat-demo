package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.entity.PushSubscription;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "웹 푸시 구독 정보 응답")
public record PushSubscriptionResponse(
        @Schema(description = "식별자")
        String id,
        @Schema(description = "계정 식별자")
        String userId,
        @Schema(description = "브라우저 푸시 서비스 주소")
        String endpoint,
        @Schema(description = "공개키 (p256dh)")
        String p256dh,
        @Schema(description = "인증 토큰 (auth)")
        String auth,
        @Schema(description = "사용자 에이전트 정보")
        String userAgent,
        @Schema(description = "기기 명칭")
        String deviceName,
        @Schema(description = "생성일시")
        LocalDateTime createdDate
) {

    public static PushSubscriptionResponse from(PushSubscription subscription) {
        return PushSubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .endpoint(subscription.getEndpoint())
                .p256dh(subscription.getP256dh())
                .auth(subscription.getAuth())
                .userAgent(subscription.getUserAgent())
                .deviceName(subscription.getDeviceName())
                .createdDate(subscription.getCreatedDate())
                .build();
    }

}

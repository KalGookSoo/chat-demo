package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.me.seesaw.domain.entity.PushProvider;
import lombok.Builder;

import java.util.Map;

@Builder
@Schema(description = "통합 푸시 기기 등록 요청")
public record PushDeviceRegisterRequest(
        @NotNull
        @Schema(description = "푸시 서비스 제공자")
        PushProvider provider,
        @Schema(description = "푸시 토큰 (EXPO)")
        String token,
        @Schema(description = "브라우저 푸시 서비스 주소 (WEB_PUSH)")
        String endpoint,
        @Schema(description = "WEB_PUSH 키 맵 (p256dh, auth)")
        Map<String, String> keys,
        @Schema(description = "사용자 에이전트 또는 플랫폼 정보")
        String platform,
        @Schema(description = "기기 식별자 또는 명칭")
        String deviceId
) {

}

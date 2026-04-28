package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.entity.PushDevice;
import kr.me.seesaw.domain.entity.PushProvider;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "통합 푸시 기기 정보 응답")
public record PushDeviceResponse(
        @Schema(description = "식별자")
        String id,
        @Schema(description = "계정 식별자")
        String userId,
        @Schema(description = "푸시 서비스 제공자")
        PushProvider provider,
        @Schema(description = "푸시 토큰")
        String pushToken,
        @Schema(description = "브라우저 푸시 서비스 주소")
        String endpoint,
        @Schema(description = "기기 명칭")
        String deviceName,
        @Schema(description = "활성 여부")
        boolean active,
        @Schema(description = "생성일시")
        LocalDateTime createdDate
) {

    public static PushDeviceResponse from(PushDevice device) {
        return PushDeviceResponse.builder()
                .id(device.getId())
                .userId(device.getUserId())
                .provider(device.getProvider())
                .pushToken(device.getPushToken())
                .endpoint(device.getEndpoint())
                .deviceName(device.getDeviceName())
                .active(device.isActive())
                .createdDate(device.getCreatedDate())
                .build();
    }

}

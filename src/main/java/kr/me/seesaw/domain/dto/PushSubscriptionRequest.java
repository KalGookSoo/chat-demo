package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "웹 푸시 구독 등록 요청")
public class PushSubscriptionRequest {

    @NotBlank
    @Schema(description = "브라우저 푸시 서비스 주소")
    private String endpoint;

    @Schema(description = "공개키 (p256dh)")
    private String p256dh;

    @Schema(description = "인증 토큰 (auth)")
    private String auth;

    @Schema(description = "사용자 에이전트 정보")
    private String userAgent;

    @Schema(description = "기기 명칭")
    private String deviceName;

}

package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "토큰 파기 요청")
public record TokenRevokeRequest(
        @NotBlank
        @Schema(description = "액세스 토큰")
        String accessToken,
        @NotBlank
        @Schema(description = "리프레시 토큰")
        String refreshToken
) {

}

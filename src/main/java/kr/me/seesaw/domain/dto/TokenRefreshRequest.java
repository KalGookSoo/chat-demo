package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * 토큰 갱신 요청 명령
 */
@Builder
@Schema(description = "토큰 갱신 요청")
public record TokenRefreshRequest(
        @NotBlank
        @Schema(description = "리프레시 토큰")
        String refreshToken
) {

}
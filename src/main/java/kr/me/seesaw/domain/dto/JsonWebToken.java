package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "JWT 토큰 정보")
public record JsonWebToken(
        @Schema(description = "액세스 토큰")
        String accessToken,
        @Schema(description = "리프레시 토큰")
        String refreshToken,
        @Schema(description = "만료 시간 (초)")
        long expiresIn
) {

}

package kr.me.seesaw.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * 토큰 갱신 요청 명령
 */
@Builder
public record TokenRefreshRequest(@NotBlank String refreshToken) {

}
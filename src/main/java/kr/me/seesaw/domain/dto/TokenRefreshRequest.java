package kr.me.seesaw.domain.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 명령
 */
public record TokenRefreshRequest(@NotBlank String refreshToken) {

}
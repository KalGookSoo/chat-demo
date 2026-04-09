package kr.me.seesaw.domain.dto;

import lombok.Builder;

@Builder
public record JsonWebToken(String accessToken, String refreshToken, long expiresIn) {

}

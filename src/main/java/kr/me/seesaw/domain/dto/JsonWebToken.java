package kr.me.seesaw.domain.dto;

public record JsonWebToken(String accessToken, String refreshToken, long expiresIn) {

}

package kr.me.seesaw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 토큰 갱신 요청 명령
 */
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    
    @NotBlank
    private String refreshToken;

}
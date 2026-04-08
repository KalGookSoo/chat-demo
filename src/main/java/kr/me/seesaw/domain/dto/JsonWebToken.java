package kr.me.seesaw.domain.dto;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JsonWebToken {

    private String accessToken;

    private String refreshToken;

    private long expiresIn;

}

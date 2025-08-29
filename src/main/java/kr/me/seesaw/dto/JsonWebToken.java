package kr.me.seesaw.dto;

import lombok.*;

import java.io.Serializable;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JsonWebToken implements Serializable {

    private String accessToken;

    private String refreshToken;

    private long expiresIn;

}

package kr.me.seesaw.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}

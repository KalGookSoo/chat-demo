package kr.me.seesaw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest implements Serializable {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String name;

}

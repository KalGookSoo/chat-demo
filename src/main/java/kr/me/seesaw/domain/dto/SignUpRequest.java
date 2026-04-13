package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "회원가입 요청")
public record SignUpRequest(
        @NotBlank
        @Schema(description = "아이디")
        String username,
        @NotBlank
        @Schema(description = "패스워드")
        String password,
        @Schema(description = "이름")
        String name
) {

}

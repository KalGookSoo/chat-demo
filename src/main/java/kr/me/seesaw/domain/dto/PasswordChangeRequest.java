package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "패스워드 변경 요청")
public record PasswordChangeRequest(
        @NotBlank
        @Schema(description = "새 패스워드")
        String newPassword
) {

}

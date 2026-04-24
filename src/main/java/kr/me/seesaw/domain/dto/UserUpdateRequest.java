package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "사용자 기본정보 수정 요청")
public record UserUpdateRequest(
        @NotBlank
        @Schema(description = "이름")
        String name
) {

}

package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.entity.User;
import lombok.Builder;

@Builder
@Schema(description = "사용자 정보 응답")
public record UserResponse(
        @Schema(description = "식별자")
        String id,
        @Schema(description = "계정명")
        String username,
        @Schema(description = "이름")
        String name
) {
}

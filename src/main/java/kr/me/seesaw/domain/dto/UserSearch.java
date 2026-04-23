package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사용자 검색 조건")
public record UserSearch(
        @Schema(description = "계정명")
        String username,
        @Schema(description = "이름")
        String name
) {

}

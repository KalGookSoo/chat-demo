package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "발신자 정보")
public record SenderResponse(
        @Schema(description = "발신자 식별자")
        String id,
        @Schema(description = "발신자 이름")
        String name
) {

}

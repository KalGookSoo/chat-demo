package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "채팅방 응답")
public record ChatRoomResponse(
        @Schema(description = "채팅방 식별자")
        String id,
        @Schema(description = "채팅방 이름")
        String name
) {

}

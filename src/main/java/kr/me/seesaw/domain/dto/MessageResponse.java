package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.vo.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "메시지 응답")
public record MessageResponse(
        @Schema(description = "메시지 식별자")
        String id,
        @Schema(description = "채팅방 식별자")
        String chatRoomId,
        @Schema(description = "메시지 내용")
        String content,
        @Schema(description = "메시지 타입")
        MessageType type,
        @Schema(description = "MIME 타입")
        String mimeType,
        @Schema(description = "생성일시")
        LocalDateTime createdDate,
        @Schema(description = "발신자 정보")
        UserResponse sender
) {

}

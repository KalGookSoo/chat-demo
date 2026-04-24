package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.vo.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "알림 응답")
public record NotificationResponse(
        @Schema(description = "알림 식별자")
        String id,
        @Schema(description = "채팅방 식별자")
        String chatRoomId,
        @Schema(description = "내용")
        String content,
        @Schema(description = "타입")
        MessageType type,
        @Schema(description = "MIME 타입")
        String mimeType,
        @Schema(description = "발생일시")
        LocalDateTime createdDate,
        @Schema(description = "발신자 정보")
        UserResponse sender
) {

}

package kr.me.seesaw.domain.dto;

import kr.me.seesaw.domain.vo.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageResponse(
        String id,
        String chatRoomId,
        String content,
        MessageType type,
        String mimeType,
        LocalDateTime createdDate,
        SenderResponse sender
) {
}

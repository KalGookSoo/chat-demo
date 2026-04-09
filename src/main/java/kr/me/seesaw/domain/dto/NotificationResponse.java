package kr.me.seesaw.domain.dto;

import kr.me.seesaw.domain.vo.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        String id,
        String chatRoomId,
        String content,
        MessageType type,
        String mimeType,
        LocalDateTime createdDate,
        SenderResponse sender
) {
}

package kr.me.seesaw.domain.dto;

import kr.me.seesaw.domain.vo.MessageType;

import java.time.LocalDateTime;

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

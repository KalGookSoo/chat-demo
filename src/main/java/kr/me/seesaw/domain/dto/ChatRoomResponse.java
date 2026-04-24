package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.entity.ChatRoom;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "채팅방 응답")
public record ChatRoomResponse(
        @Schema(description = "채팅방 식별자")
        String id,
        @Schema(description = "채팅방 이름")
        String name,
        @Schema(description = "생성자 식별자")
        String createdBy,
        @Schema(description = "생성 일시")
        LocalDateTime createdDate,
        @Schema(description = "참여자 목록")
        List<UserResponse> members
) {

    public static ChatRoomResponseBuilder from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .createdBy(chatRoom.getCreatedBy())
                .createdDate(chatRoom.getCreatedDate());
    }

}

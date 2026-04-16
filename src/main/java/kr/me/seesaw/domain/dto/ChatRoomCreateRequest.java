package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "채팅방 생성 요청")
public record ChatRoomCreateRequest(
        @NotBlank(message = "채팅방 이름은 필수입니다.")
        @Schema(description = "채팅방 이름", example = "점심 메뉴 정하기")
        String name,
        @Schema(description = "초대할 친구 ID 목록", example = "[\"user-uuid-1\", \"user-uuid-2\"]")
        List<String> friendIds
) {

}

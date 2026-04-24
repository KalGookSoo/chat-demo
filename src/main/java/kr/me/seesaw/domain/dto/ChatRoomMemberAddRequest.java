package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "채팅방 멤버 추가 요청")
public record ChatRoomMemberAddRequest(
        @NotEmpty
        @Schema(description = "추가할 사용자 식별자 목록")
        List<@NotBlank String> memberIds
) {

}

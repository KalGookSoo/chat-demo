package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.vo.FriendStatus;
import lombok.Builder;

@Builder
@Schema(description = "친구 정보 응답")
public record FriendResponse(
        @Schema(description = "내 식별자")
        String userId,
        @Schema(description = "친구")
        UserResponse friend,
        @Schema(description = "상태")
        FriendStatus status
) {

}

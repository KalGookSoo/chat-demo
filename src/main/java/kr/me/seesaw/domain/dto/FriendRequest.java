package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 요청")
public record FriendRequest(
        @Schema(description = "친구 계정명")
        String username
) {

}

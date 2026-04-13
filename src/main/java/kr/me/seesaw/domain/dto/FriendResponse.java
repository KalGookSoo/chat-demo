package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.entity.Friend;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.domain.vo.FriendStatus;
import lombok.Builder;

@Builder
@Schema(description = "친구 정보 응답")
public record FriendResponse(
        @Schema(description = "관계 식별자")
        String id,
        @Schema(description = "친구 아이디")
        String friendId,
        @Schema(description = "친구 이름")
        String friendName,
        @Schema(description = "상태")
        FriendStatus status
) {

    public static FriendResponse from(Friend friend, String currentUserId) {
        User friendUser = friend.getId().equals(currentUserId) ? friend.getFriend() : friend.getUser();
        return FriendResponse.builder()
                .id(friend.getId())
                .friendId(friendUser.getUsername())
                .friendName(friendUser.getName())
                .status(friend.getStatus())
                .build();
    }

}

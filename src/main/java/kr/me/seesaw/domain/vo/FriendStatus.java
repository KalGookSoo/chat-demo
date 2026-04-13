package kr.me.seesaw.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "친구 상태")
public enum FriendStatus {
    PENDING("요청 대기"),
    ACCEPTED("수락됨"),
    BLOCKED("차단됨");

    private final String description;
}

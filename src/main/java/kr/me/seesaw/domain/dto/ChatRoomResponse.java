package kr.me.seesaw.domain.dto;

import lombok.Builder;

@Builder
public record ChatRoomResponse(String id, String name) {

}

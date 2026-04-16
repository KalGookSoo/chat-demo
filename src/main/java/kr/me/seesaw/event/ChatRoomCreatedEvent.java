package kr.me.seesaw.event;

import java.util.List;

public record ChatRoomCreatedEvent(String chatRoomId, String creatorId, List<String> friendIds) {

}

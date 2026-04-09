package kr.me.seesaw.service;

import kr.me.seesaw.domain.entity.ChatRoom;
import kr.me.seesaw.domain.dto.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {

    void createDemoChatRooms();

    void createChatRoom(String name);

    List<ChatRoom> getAllChatRooms();

    void addMember(String chatRoomId, String memberId);

    List<ChatRoomResponse> getChatRoomsByUserId(String userId);

}

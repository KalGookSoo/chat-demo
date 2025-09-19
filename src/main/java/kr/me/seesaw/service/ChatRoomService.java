package kr.me.seesaw.service;

import kr.me.seesaw.domain.ChatRoom;
import kr.me.seesaw.dto.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {

    void createDemoChatRooms();

    void createChatRoom(String name);

    List<ChatRoom> getAllChatRooms();

    void addMember(String id, String memberId);

    List<ChatRoomResponse> getChatRoomsByUserId(String userId);

}

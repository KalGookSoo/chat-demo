package kr.me.seesaw.service;

import kr.me.seesaw.domain.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    void createDemoChatRooms();

    ChatRoom createChatRoom(String name);

    List<ChatRoom> getAllChatRooms();

    void addMember(String id, String memberId);
}

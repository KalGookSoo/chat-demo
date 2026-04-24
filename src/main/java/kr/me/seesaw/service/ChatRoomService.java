package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.ChatRoomResponse;
import kr.me.seesaw.domain.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {

    void createChatRoom(String name);

    ChatRoomResponse createChatRoom(String name, String creatorId, List<String> friendIds);

    List<ChatRoom> getAllChatRooms();

    void addMember(String chatRoomId, String memberId);

    List<ChatRoomResponse> getChatRoomsByUserId(String userId);

    ChatRoomResponse getChatRoom(String chatRoomId);

}

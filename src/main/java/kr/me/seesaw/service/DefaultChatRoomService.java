package kr.me.seesaw.service;

import kr.me.seesaw.domain.entity.ChatRoom;
import kr.me.seesaw.domain.entity.ChatRoomMember;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.domain.dto.ChatRoomResponse;
import kr.me.seesaw.repository.ChatRoomMemberRepository;
import kr.me.seesaw.repository.ChatRoomRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultChatRoomService implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public void createDemoChatRooms() {
        createChatRoom("채팅방1");
        createChatRoom("채팅방2");
    }

    @Override
    public void createChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public void addMember(String id, String memberId) {
        ChatRoom chatRoom = chatRoomRepository.getReferenceById(id);
        User user = userRepository.getReferenceById(memberId);

        // // TODO 이벤트로 대체할 것
        ChatRoomMember chatRoomMember = new ChatRoomMember();
        chatRoomMember.setChatRoom(chatRoom);
        chatRoomMember.setUser(user);
        chatRoomMemberRepository.save(chatRoomMember);
    }

    @Override
    public List<ChatRoomResponse> getChatRoomsByUserId(String userId) {
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByUserId(userId);
        return chatRoomMembers.stream().map(ChatRoomMember::getChatRoom).map(chatRoom -> new ChatRoomResponse(chatRoom.getId(), chatRoom.getName())).toList();
    }

}

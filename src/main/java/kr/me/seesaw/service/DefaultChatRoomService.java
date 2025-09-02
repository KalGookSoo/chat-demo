package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.domain.ChatRoom;
import kr.me.seesaw.domain.ChatRoomMember;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultChatRoomService implements ChatRoomService {
    private final EntityManager entityManager;

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void createDemoChatRooms() {
        createChatRoom("채팅방1");
        createChatRoom("채팅방2");
    }

    @Override
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        entityManager.persist(chatRoom);
        return chatRoom;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public void addMember(String id, String memberId) {
        ChatRoom chatRoom = Optional.ofNullable(entityManager.getReference(ChatRoom.class, id))
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채팅방입니다. id: " + id));

        User user = Optional.ofNullable(entityManager.getReference(User.class, memberId))
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다. id: " + memberId));
        ChatRoomMember chatRoomMember = new ChatRoomMember(chatRoom, user.getId());
        chatRoom.addMember(chatRoomMember);
        entityManager.persist(chatRoomMember);
    }

    @Override
    public ChatRoom getChatRoom(String chatRoomId) {
        return Optional.ofNullable(entityManager.find(ChatRoom.class, chatRoomId))
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채팅방입니다. id: " + chatRoomId));
    }
}

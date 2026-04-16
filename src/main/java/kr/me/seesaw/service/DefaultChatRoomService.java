package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.ChatRoomResponse;
import kr.me.seesaw.domain.entity.ChatRoom;
import kr.me.seesaw.domain.entity.ChatRoomMember;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.event.ChatRoomCreatedEvent;
import kr.me.seesaw.repository.ChatRoomMemberRepository;
import kr.me.seesaw.repository.ChatRoomRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DefaultChatRoomService implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void createChatRoom(String name) {
        log.info("채팅방을 생성합니다. name: {}", name);
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .build();
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom createChatRoom(String name, String creatorId, List<String> friendIds) {
        log.info("채팅방을 생성하고 친구를 초대합니다. name: {}, creatorId: {}, friendCount: {}", name, creatorId, friendIds == null ? 0 : friendIds.size());
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        eventPublisher.publishEvent(new ChatRoomCreatedEvent(savedChatRoom.getId(), creatorId, friendIds));

        return savedChatRoom;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoom> getAllChatRooms() {
        log.debug("모든 채팅방을 조회합니다.");
        return chatRoomRepository.findAll();
    }

    @Override
    public void addMember(String chatRoomId, String memberId) {
        if (chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoomId, memberId).isPresent()) {
            log.info("이미 채팅방에 존재하는 멤버입니다. chatRoomId: {}, memberId: {}", chatRoomId, memberId);
            return;
        }
        log.info("채팅방에 멤버를 추가합니다. chatRoomId: {}, memberId: {}", chatRoomId, memberId);
        ChatRoom chatRoom = chatRoomRepository.getReferenceById(chatRoomId);
        User user = userRepository.getReferenceById(memberId);

        ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatRoomMemberRepository.save(chatRoomMember);
    }

    @Override
    public List<ChatRoomResponse> getChatRoomsByUserId(String userId) {
        log.debug("유저가 속한 채팅방을 조회합니다. userId: {}", userId);
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByUserId(userId);
        return chatRoomMembers.stream().map(ChatRoomMember::getChatRoom).map(chatRoom -> new ChatRoomResponse(chatRoom.getId(), chatRoom.getName())).toList();
    }

}

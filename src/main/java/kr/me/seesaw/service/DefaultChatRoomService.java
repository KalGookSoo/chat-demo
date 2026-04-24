package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.ChatRoomResponse;
import kr.me.seesaw.domain.dto.UserResponse;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public ChatRoomResponse createChatRoom(String name, String creatorId, List<String> friendIds) {
        log.info("채팅방을 생성하고 친구를 초대합니다. name: {}, creatorId: {}, friendCount: {}", name, creatorId, friendIds == null ? 0 : friendIds.size());
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        eventPublisher.publishEvent(new ChatRoomCreatedEvent(savedChatRoom.getId(), creatorId, friendIds));

        return ChatRoomResponse.from(savedChatRoom)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoom> getAllChatRooms() {
        log.debug("모든 채팅방을 조회합니다.");
        return chatRoomRepository.findAll();
    }

    @Override
    public void addMember(String chatRoomId, String memberId) {
        log.debug("채팅방 멤버 존재 여부를 조회합니다. chatRoomId: {}, memberId: {}", chatRoomId, memberId);
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
    public void addMembers(String chatRoomId, List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        log.info("채팅방에 멤버를 일괄 추가합니다. chatRoomId: {}, 대상 수: {}", chatRoomId, memberIds.size());
        memberIds.stream()
                .distinct()
                .forEach(memberId -> addMember(chatRoomId, memberId));
    }

    @Override
    public List<ChatRoomResponse> getChatRoomsByUserId(String userId) {
        log.debug("유저가 속한 채팅방을 조회합니다. userId: {}", userId);
        List<ChatRoomMember> memberships = chatRoomMemberRepository.findAllByUserId(userId);
        if (memberships.isEmpty()) {
            return List.of();
        }

        List<String> chatRoomIds = memberships.stream()
                .map(ChatRoomMember::getChatRoomId)
                .distinct()
                .toList();

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByIdIn(chatRoomIds);
        Map<String, List<UserResponse>> membersByChatRoomId = getMembersByChatRoomId(chatRoomIds);
        Map<String, ChatRoom> chatRoomById = chatRooms.stream()
                .collect(Collectors.toMap(ChatRoom::getId, Function.identity()));

        return chatRoomIds.stream()
                .map(chatRoomById::get)
                .filter(java.util.Objects::nonNull)
                .map(chatRoom -> ChatRoomResponse.from(chatRoom)
                        .members(membersByChatRoomId.getOrDefault(chatRoom.getId(), List.of()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ChatRoomResponse getChatRoom(String chatRoomId) {
        log.debug("채팅방 상세 정보를 조회합니다. chatRoomId: {}", chatRoomId);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("채팅방을 찾을 수 없습니다. id: " + chatRoomId));

        List<String> chatRoomMemberIds = chatRoomMemberRepository.findAllByChatRoomId(chatRoomId)
                .stream()
                .map(ChatRoomMember::getUserId)
                .toList();
        List<UserResponse> chatRoomMembers = getUserResponses(chatRoomMemberIds);

        return ChatRoomResponse.from(chatRoom)
                .members(chatRoomMembers)
                .build();
    }

    private Map<String, List<UserResponse>> getMembersByChatRoomId(Collection<String> chatRoomIds) {
        List<ChatRoomMember> allMembers = chatRoomMemberRepository.findAllByChatRoomIdIn(chatRoomIds);
        if (allMembers.isEmpty()) {
            return Map.of();
        }

        Set<String> userIds = allMembers.stream()
                .map(ChatRoomMember::getUserId)
                .collect(Collectors.toSet());

        Map<String, UserResponse> userResponseById = getUserResponses(userIds).stream()
                .collect(Collectors.toMap(UserResponse::id, Function.identity()));

        return allMembers.stream()
                .collect(Collectors.groupingBy(ChatRoomMember::getChatRoomId,
                        Collectors.mapping(member -> userResponseById.get(member.getUserId()),
                                Collectors.filtering(java.util.Objects::nonNull, Collectors.toList()))));
    }

    private List<UserResponse> getUserResponses(Collection<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.findAllByIdIn(userIds);
        return users.stream()
                .map(UserResponse::from)
                .map(UserResponse.UserResponseBuilder::build)
                .toList();
    }

}

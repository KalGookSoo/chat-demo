package kr.me.seesaw.event;

import kr.me.seesaw.domain.vo.FriendStatus;
import kr.me.seesaw.repository.FriendRepository;
import kr.me.seesaw.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomEventListener {

    private final ChatRoomService chatRoomService;

    private final FriendRepository friendRepository;

    @EventListener
    public void onChatRoomCreated(ChatRoomCreatedEvent event) {
        log.info("채팅방 생성 이벤트를 수신했습니다. chatRoomId: {}, creatorId: {}", event.chatRoomId(), event.creatorId());

        // 생성자 추가
        chatRoomService.addMember(event.chatRoomId(), event.creatorId());

        // 친구 초대
        if (event.friendIds() != null && !event.friendIds().isEmpty()) {
            event.friendIds().forEach(friendId -> {
                // 현재 친구 목록에 존재하는지 검증 (양방향 ACCEPTED 상태 확인)
                boolean isFriend = friendRepository.findByUserIdAndFriendId(event.creatorId(), friendId)
                        .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                        .isPresent() ||
                        friendRepository.findByUserIdAndFriendId(friendId, event.creatorId())
                                .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                                .isPresent();

                if (isFriend) {
                    chatRoomService.addMember(event.chatRoomId(), friendId);
                } else {
                    log.warn("친구가 아닌 사용자는 초대할 수 없습니다. creatorId: {}, targetId: {}", event.creatorId(), friendId);
                }
            });
        }
    }

}

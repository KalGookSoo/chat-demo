package kr.me.seesaw.service;

import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.FriendResponse;
import kr.me.seesaw.domain.entity.Friend;
import kr.me.seesaw.domain.vo.FriendStatus;
import kr.me.seesaw.repository.FriendRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DefaultFriendService implements FriendService {

    private final FriendRepository friendRepository;

    private final UserRepository userRepository;

    private final PrincipalProvider principalProvider;

    @Override
    public void requestFriend(String friendId) {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        log.info("{}가 {}에게 친구 요청을 보냅니다.", authentication.getName(), friendId);
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        if (friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈거나 친구 상태입니다.");
        }

        if (friendRepository.existsByUserIdAndFriendId(friendId, userId)) {
            throw new IllegalArgumentException("상대방이 이미 친구 요청을 보냈습니다. 수락해주세요.");
        }

        Friend request = Friend.builder()
                .user(userRepository.getReferenceById(userId))
                .friend(userRepository.getReferenceById(friendId))
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(request);
    }

    @Override
    public void acceptFriend(String friendId) {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();

        Friend friend = friendRepository.findByUserIdAndFriendId(friendId, userId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        if (!friend.isPending()) {
            throw new IllegalArgumentException("대기 중인 요청이 아닙니다.");
        }

        log.debug("{}가 {}의 친구 요청을 수락합니다.", authentication.getName(), friendId);
        friend.accept();
    }

    @Override
    public void removeFriend(String friendId) {
        log.info("{}가 {}에게 요청한 친구 요청을 삭제합니다.", principalProvider.getAuthentication().getName(), friendId);
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresent(friendRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends() {
        log.debug("{}의 친구 목록을 조회합니다.", principalProvider.getAuthentication().getName());
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        return friendRepository.findByUserIdAndStatusOrFriendIdAndStatus(userId, FriendStatus.ACCEPTED, userId, FriendStatus.ACCEPTED)
                .stream()
                .map(friend -> FriendResponse.from(friend, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getPendingRequests() {
        log.debug("{}의 친구 요청 목록을 조회합니다.", principalProvider.getAuthentication().getName());
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        // 나에게 온 요청 (다른 사용자가 나를 friend_id로 지정하고 status가 PENDING인 경우)
        return friendRepository.findByFriendIdAndStatus(userId, FriendStatus.PENDING)
                .stream()
                .map(friend -> FriendResponse.from(friend, userId))
                .collect(Collectors.toList());
    }

}

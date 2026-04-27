package kr.me.seesaw.service;

import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.FriendResponse;
import kr.me.seesaw.domain.dto.UserResponse;
import kr.me.seesaw.domain.entity.Friend;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.domain.vo.FriendStatus;
import kr.me.seesaw.repository.FriendRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DefaultFriendService implements FriendService {

    private final FriendRepository friendRepository;

    private final UserRepository userRepository;

    private final PrincipalProvider principalProvider;

    @Override
    public void requestFriend(String username) {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        log.info("{}가 {}에게 친구 요청을 보냅니다.", authentication.getName(), username);

        User friend = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (userId.equals(friend.getId())) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        if (friendRepository.existsByUserIdAndFriendId(userId, friend.getId())) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈거나 친구 상태입니다.");
        }

        if (friendRepository.existsByUserIdAndFriendId(friend.getId(), userId)) {
            throw new IllegalArgumentException("상대방이 이미 친구 요청을 보냈습니다. 수락해주세요.");
        }

        Friend request = Friend.builder()
                .userId(userId)
                .friendId(friend.getId())
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
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        log.info("{}가 {}에게 요청한 친구 요청을 삭제합니다.", userId, friendId);

        // 내가 보낸 관계 또는 나에게 온 관계 모두 삭제/거절 가능
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresentOrElse(friendRepository::delete, () -> friendRepository.findByUserIdAndFriendId(friendId, userId)
                        .ifPresent(friendRepository::delete));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends() {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        log.debug("{}의 친구 목록을 조회합니다.", authentication.getName());

        List<Friend> friends = friendRepository.findByUserIdOrFriendId(userId, userId);
        if (friends.isEmpty()) {
            return List.of();
        }

        Set<String> friendUserIds = friends.stream()
                .flatMap(f -> Stream.of(f.getUserId(), f.getFriendId()))
                .filter(id -> !id.equals(userId))
                .collect(Collectors.toSet());

        Map<String, User> userMap = userRepository.findAllByIdIn(friendUserIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return friends.stream()
                .map(f -> {
                    String requesterId = f.getCreatedBy();
                    String targetFriendId = resolveFriendId(userId, requesterId, f.getFriendId());
                    User friendUser = userMap.get(targetFriendId);
                    if (friendUser == null) {
                        throw new IllegalStateException("친구 사용자 정보를 찾을 수 없습니다. id=" + targetFriendId);
                    }
                    UserResponse friend = UserResponse.from(friendUser)
                            .build();
                    return FriendResponse.builder()
                            .userId(userId)
                            .requesterId(requesterId)
                            .friend(friend)
                            .status(f.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String resolveFriendId(String userId, String requesterId, String friendId) {
        return userId.equals(requesterId) ? friendId : requesterId;
    }

    @Override
    public void blockFriend(String friendId) {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();

        Friend friend = friendRepository.findByUserIdAndFriendId(friendId, userId)
                .orElseThrow(() -> new IllegalArgumentException("친구 또는 친구 요청이 존재하지 않습니다."));

        if (friend.isBlocked()) {
            throw new IllegalArgumentException("이미 차단된 친구입니다.");
        }

        log.debug("{}가 {} 친구를 차단합니다.", authentication.getName(), friendId);
        friend.block();
    }

}

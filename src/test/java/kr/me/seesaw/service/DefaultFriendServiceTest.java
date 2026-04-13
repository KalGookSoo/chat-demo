package kr.me.seesaw.service;

import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.FriendResponse;
import kr.me.seesaw.domain.dto.UserPrincipal;
import kr.me.seesaw.domain.entity.Friend;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.domain.vo.FriendStatus;
import kr.me.seesaw.repository.FriendRepository;
import kr.me.seesaw.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultFriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PrincipalProvider principalProvider;

    @InjectMocks
    private DefaultFriendService friendService;

    private User user;

    private User friendUser;

    @BeforeEach
    void setUp() throws Exception {
        user = User.create("me", "pass", "Me");
        setId(user, "myId");
        friendUser = User.create("friend", "pass", "Friend");
        setId(friendUser, "friendId");
    }

    private void setId(User user, String id) throws Exception {
        java.lang.reflect.Field field = kr.me.seesaw.domain.entity.BaseEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, id);
    }

    private void mockCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal(user);
        given(authentication.getPrincipal()).willReturn(principal);
        given(principalProvider.getAuthentication()).willReturn(authentication);
    }

    @Test
    @DisplayName("친구 요청 성공")
    void requestFriend_Success() {
        // given
        mockCurrentUser();
        given(friendRepository.existsByUserIdAndFriendId("myId", "friendId")).willReturn(false);
        given(friendRepository.existsByUserIdAndFriendId("friendId", "myId")).willReturn(false);
        given(userRepository.getReferenceById("myId")).willReturn(user);
        given(userRepository.getReferenceById("friendId")).willReturn(friendUser);

        // when
        friendService.requestFriend("friendId");

        // then
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("자기 자신에게 친구 요청 시 예외 발생")
    void requestFriend_SelfRequest_ThrowsException() {
        // given
        mockCurrentUser();

        // when & then
        assertThatThrownBy(() -> friendService.requestFriend("myId"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미 친구 요청을 보낸 경우 예외 발생")
    void requestFriend_AlreadyRequested_ThrowsException() {
        // given
        mockCurrentUser();
        given(friendRepository.existsByUserIdAndFriendId("myId", "friendId")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> friendService.requestFriend("friendId"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("친구 수락 성공")
    void acceptFriend_Success() {
        // given
        mockCurrentUser();
        Friend relationship = Friend.builder()
                .user(friendUser)
                .friend(user)
                .status(FriendStatus.PENDING)
                .build();
        given(friendRepository.findByUserIdAndFriendId("friendId", "myId")).willReturn(Optional.of(relationship));

        // when
        friendService.acceptFriend("friendId");

        // then
        assertThat(relationship.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
    }

    @Test
    @DisplayName("친구 목록 조회 성공")
    void getFriends_Success() {
        // given
        mockCurrentUser();
        Friend relationship = Friend.builder()
                .user(user)
                .friend(friendUser)
                .status(FriendStatus.ACCEPTED)
                .build();
        given(friendRepository.findByUserIdAndStatusOrFriendIdAndStatus("myId", FriendStatus.ACCEPTED, "myId", FriendStatus.ACCEPTED))
                .willReturn(List.of(relationship));

        // when
        List<FriendResponse> friends = friendService.getFriends();

        // then
        assertThat(friends).hasSize(1);
    }

}

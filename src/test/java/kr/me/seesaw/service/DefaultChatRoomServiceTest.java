package kr.me.seesaw.service;

import kr.me.seesaw.TestDataInitializerConfig;
import kr.me.seesaw.domain.entity.ChatRoom;
import kr.me.seesaw.domain.entity.ChatRoomMember;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.ChatRoomMemberRepository;
import kr.me.seesaw.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataInitializerConfig.class)
@Transactional
class DefaultChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Test
    @DisplayName("채팅방을 생성하고 친구를 초대하면 생성자와 친구가 멤버로 등록되어야 한다.")
    void createChatRoomWithFriendsTest() {
        // given
        User creator = userRepository.findByUsername("user1").orElseThrow();
        User friend = userRepository.findByUsername("user2").orElseThrow();
        User notFriend = userRepository.findByUsername("user3").orElseThrow();

        String chatRoomName = "서비스 테스트 채팅방";
        List<String> friendIds = List.of(friend.getId(), notFriend.getId());

        // when
        ChatRoom chatRoom = chatRoomService.createChatRoom(chatRoomName, creator.getId(), friendIds);

        // then
        assertThat(chatRoom.getName()).isEqualTo(chatRoomName);

        // 멤버 확인
        Optional<ChatRoomMember> creatorMember = chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoom.getId(), creator.getId());
        assertThat(creatorMember).isPresent();

        Optional<ChatRoomMember> friendMember = chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoom.getId(), friend.getId());
        assertThat(friendMember).isPresent();

        Optional<ChatRoomMember> notFriendMember = chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoom.getId(), notFriend.getId());
        assertThat(notFriendMember).isEmpty(); // user3은 user1과 친구가 아니므로 초대되지 않아야 함
    }

}
package kr.me.seesaw.service;

import kr.me.seesaw.TestDataInitializerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@DataJpaTest
@Import(TestDataInitializerConfig.class)
class DefaultFriendServiceTest {

    @BeforeEach
    void setUp() {

    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("친구 요청 성공")
    void requestFriend_Success() {

    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("자기 자신에게 친구 요청 시 예외 발생")
    void requestFriend_SelfRequest_ThrowsException() {

    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("이미 친구 요청을 보낸 경우 예외 발생")
    void requestFriend_AlreadyRequested_ThrowsException() {

    }

    @WithMockUser(username = "user2")
    @Test
    @DisplayName("친구 수락 성공")
    void acceptFriend_Success() {

    }

    @WithMockUser(username = "user2")
    @Test
    @DisplayName("친구 목록 조회 성공")
    void getFriends_Success() {

    }

}

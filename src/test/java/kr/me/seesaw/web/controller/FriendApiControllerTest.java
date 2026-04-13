package kr.me.seesaw.web.controller;

import kr.me.seesaw.TestDataInitializerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestDataInitializerConfig.class)
class FriendApiControllerTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("친구 요청 API 성공")
    void requestFriend_Success() throws Exception {

    }

    @Test
    @DisplayName("친구 수락 API 성공")
    void acceptFriend_Success() throws Exception {

    }

    @Test
    @DisplayName("친구 삭제/거절 API 성공")
    void removeFriend_Success() throws Exception {

    }

    @Test
    @DisplayName("친구 목록 조회 API 성공")
    void getFriends_Success() throws Exception {

    }

    @Test
    @DisplayName("대기 중인 요청 조회 API 성공")
    void getPendingRequests_Success() throws Exception {

    }

}

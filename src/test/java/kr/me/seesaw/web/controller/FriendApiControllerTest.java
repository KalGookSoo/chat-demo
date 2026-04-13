package kr.me.seesaw.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.component.security.JwtTokenProvider;
import kr.me.seesaw.domain.dto.FriendRequest;
import kr.me.seesaw.domain.dto.UserPrincipal;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class FriendApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private User user;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        user = User.create("testuser", "password", "Test User");
        principal = new UserPrincipal(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("친구 요청 API 성공")
    void requestFriend_Success() throws Exception {
        FriendRequest request = new FriendRequest("friendId");

        mockMvc.perform(post("/api/friends/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("친구 수락 API 성공")
    void acceptFriend_Success() throws Exception {
        mockMvc.perform(put("/api/friends/friendId/accept"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("친구 삭제/거절 API 성공")
    void removeFriend_Success() throws Exception {
        mockMvc.perform(delete("/api/friends/friendId"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("친구 목록 조회 API 성공")
    void getFriends_Success() throws Exception {
        given(friendService.getFriends()).willReturn(List.of());

        mockMvc.perform(get("/api/friends"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("대기 중인 요청 조회 API 성공")
    void getPendingRequests_Success() throws Exception {
        given(friendService.getPendingRequests()).willReturn(List.of());

        mockMvc.perform(get("/api/friends/pending"))
                .andExpect(status().isOk());
    }

}

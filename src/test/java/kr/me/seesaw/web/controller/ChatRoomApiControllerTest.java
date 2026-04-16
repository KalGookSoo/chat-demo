package kr.me.seesaw.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.TestDataInitializerConfig;
import kr.me.seesaw.domain.dto.ChatRoomCreateRequest;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestDataInitializerConfig.class)
@Transactional
class ChatRoomApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("채팅방 생성 API - 성공 (친구 초대 포함)")
    @WithMockUser(username = "user1", roles = "USER")
    void createChatRoom_Success() throws Exception {
        // given
        User user1 = userRepository.findByUsername("user1").orElseThrow();
        User user2 = userRepository.findByUsername("user2").orElseThrow();

        ChatRoomCreateRequest request = new ChatRoomCreateRequest("새로운 채팅방", List.of(user2.getId()));

        // when & then
        mockMvc.perform(post("/api/chat-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("새로운 채팅방"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("채팅방 생성 API - 권한 없음 (인증되지 않은 사용자)")
    void createChatRoom_Unauthorized() throws Exception {
        // given
        ChatRoomCreateRequest request = new ChatRoomCreateRequest("익명 방", null);

        // when & then
        mockMvc.perform(post("/api/chat-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

}

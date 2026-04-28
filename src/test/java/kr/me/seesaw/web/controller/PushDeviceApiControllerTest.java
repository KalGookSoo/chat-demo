package kr.me.seesaw.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.TestDataInitializerConfig;
import kr.me.seesaw.domain.dto.PushDeviceRegisterRequest;
import kr.me.seesaw.domain.entity.PushProvider;
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

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestDataInitializerConfig.class)
@Transactional
class PushDeviceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("WEB_PUSH 기기를 등록하면 201 응답과 생성된 정보를 반환한다")
    @WithMockUser(username = "user1")
    void register_WebPush_Success() throws Exception {
        // given
        PushDeviceRegisterRequest request = PushDeviceRegisterRequest.builder()
                .provider(PushProvider.WEB_PUSH)
                .endpoint("https://new-endpoint")
                .keys(Map.of("p256dh", "new-p256dh", "auth", "new-auth"))
                .platform("Chrome")
                .deviceId("My PC")
                .build();

        // when & then
        mockMvc.perform(post("/api/push/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider").value("WEB_PUSH"))
                .andExpect(jsonPath("$.endpoint").value("https://new-endpoint"))
                .andExpect(jsonPath("$.deviceName").value("My PC"));
    }

    @Test
    @DisplayName("EXPO 푸시 토큰을 등록하면 201 응답과 생성된 정보를 반환한다")
    @WithMockUser(username = "user1")
    void register_ExpoPush_Success() throws Exception {
        // given
        PushDeviceRegisterRequest request = PushDeviceRegisterRequest.builder()
                .provider(PushProvider.EXPO)
                .token("ExponentPushToken[new-token]")
                .platform("Android")
                .deviceId("Galaxy S24")
                .build();

        // when & then
        mockMvc.perform(post("/api/push/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider").value("EXPO"))
                .andExpect(jsonPath("$.pushToken").value("ExponentPushToken[new-token]"))
                .andExpect(jsonPath("$.deviceName").value("Galaxy S24"));
    }

    @Test
    @DisplayName("내 푸시 기기 목록을 조회하면 초기 데이터와 함께 반환한다")
    @WithMockUser(username = "user1")
    void getMyDevices_Success() throws Exception {
        // user1은 TestDataInitializerConfig에서 webDevice, expoDevice 2개가 등록되어 있음
        mockMvc.perform(get("/api/push/devices/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.provider == 'WEB_PUSH')]").exists())
                .andExpect(jsonPath("$[?(@.provider == 'EXPO')]").exists());
    }

    @Test
    @DisplayName("기기 등록을 해제하면 204 응답을 반환한다")
    @WithMockUser(username = "user1")
    void unregister_Success() throws Exception {
        // given: 먼저 기기 목록 조회하여 ID 획득
        String content = mockMvc.perform(get("/api/push/devices/me"))
                .andReturn().getResponse().getContentAsString();
        String deviceId = objectMapper.readTree(content).get(0).get("id").asText();

        // when & then
        mockMvc.perform(delete("/api/push/devices/" + deviceId))
                .andExpect(status().isNoContent());

        // then: 삭제 확인
        mockMvc.perform(get("/api/push/devices/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

}

package kr.me.seesaw.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.PushSubscriptionRequest;
import kr.me.seesaw.domain.dto.PushSubscriptionResponse;
import kr.me.seesaw.service.PushSubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PushSubscriptionApiControllerTest {

    private final String userId = "user-id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PushSubscriptionService pushSubscriptionService;

    @MockitoBean
    private PrincipalProvider principalProvider;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getDetails()).thenReturn(userId);
        when(principalProvider.getAuthentication()).thenReturn(authentication);
    }

    @Test
    @DisplayName("웹 푸시 구독 등록 요청이 성공하면 200 응답과 구독 정보를 반환한다")
    @WithMockUser
    void subscribe_Success_ReturnsSubscriptionInfo() throws Exception {
        // given
        PushSubscriptionRequest request = PushSubscriptionRequest.builder()
                .endpoint("https://fcm.googleapis.com/fcm/send/endpoint-123")
                .p256dh("p256dh-key")
                .auth("auth-token")
                .userAgent("Mozilla/5.0...")
                .deviceName("My iPhone")
                .build();

        PushSubscriptionResponse response = PushSubscriptionResponse.builder()
                .id("subscription-id")
                .userId(userId)
                .endpoint(request.getEndpoint())
                .p256dh(request.getP256dh())
                .auth(request.getAuth())
                .userAgent(request.getUserAgent())
                .deviceName(request.getDeviceName())
                .createdDate(LocalDateTime.now())
                .build();

        when(pushSubscriptionService.subscribe(eq(userId), any(PushSubscriptionRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/push/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("subscription-id"))
                .andExpect(jsonPath("$.endpoint").value(request.getEndpoint()))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("웹 푸시 구독 취소 요청이 성공하면 204 응답을 반환한다")
    @WithMockUser
    void unsubscribe_Success_Returns204() throws Exception {
        // given
        String endpoint = "https://fcm.googleapis.com/fcm/send/endpoint-123";

        // when & then
        mockMvc.perform(delete("/api/push/subscriptions")
                        .param("endpoint", endpoint))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 구독을 시도하면 401 응답을 반환한다")
    void subscribe_Unauthenticated_Returns401() throws Exception {
        // given
        PushSubscriptionRequest request = PushSubscriptionRequest.builder()
                .endpoint("https://endpoint")
                .build();

        // when & then
        mockMvc.perform(post("/api/push/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

}

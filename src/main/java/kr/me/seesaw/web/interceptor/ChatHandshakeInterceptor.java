package kr.me.seesaw.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {
        if (!(attributes.get("authentication") instanceof Authentication)) {
            logger.warn("핸드쉐이크 거부: 인증 정보를 찾을 수 없습니다. IP: {}", request.getRemoteAddress());
            return false;
        }

        String chatRoomId = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("chatRoomId");
        if (chatRoomId == null || chatRoomId.isBlank()) {
            logger.warn("핸드쉐이크 거부: chatRoomId가 없습니다. IP={}", request.getRemoteAddress());
            return false;
        }
        attributes.put("chatRoomId", chatRoomId);
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {

    }

}

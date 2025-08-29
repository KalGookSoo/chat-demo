package kr.me.seesaw;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    private final StringRedisTemplate redisTemplate;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        // TODO
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage textMessage) {
        // TODO 채팅방 식별
        // TODO 채팅방에 메시지 전송
        // TODO 채팅방에 구독한 구독자들에게 푸시 알림
    }


    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        // TODO
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        // TODO
    }

}

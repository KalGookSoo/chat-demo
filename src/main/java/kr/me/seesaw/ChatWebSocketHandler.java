package kr.me.seesaw;

import kr.me.seesaw.entity.Message;
import kr.me.seesaw.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final MessageRepository messageRepository;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        logger.info("선수 입장: {}", session.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        logger.info("페이로드: {}", payload);

        Message chatMessage = new Message(payload, session.getId());
        messageRepository.save(chatMessage);
        logger.info("메시지 저장됨: {}", chatMessage);

        TextMessage broadcastMessage = new TextMessage(payload);
        sessions.values()
                .stream()
                .filter(WebSocketSession::isOpen)
                .forEach(s -> {
                    try {
                        s.sendMessage(broadcastMessage);
                    } catch (IOException e) {
                        logger.error("전송실패: {}", e.getMessage());
                    }
                });
    }

    @Override
    protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message) throws Exception {
        // TODO Implement
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) throws Exception {
        logger.error("오류 발생: {}", exception.getMessage());
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        WebSocketSession removed = sessions.remove(session.getId());
        if (removed != null && removed.isOpen()) {
            removed.close();
        }
        logger.info("연결 종료: {}, 상태: {}", session.getId(), status);
        super.afterConnectionClosed(session, status);
    }

}

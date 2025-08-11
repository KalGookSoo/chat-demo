package kr.me.seesaw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        logger.info("선수 입장: {}", session.getId());

        List<Message> messages = messageRepository.findTop100ByOrderByTimestampDesc();
        
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("messages", messages);
            
            String jsonMessages = objectMapper.writeValueAsString(messageData);
            TextMessage textMessage = new TextMessage(jsonMessages);
            session.sendMessage(textMessage);
            logger.info("메시지 일괄 전송 완료: {} 개", messages.size());
        } catch (JsonProcessingException e) {
            logger.error("메시지 JSON 변환 실패: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("메시지 전송 실패: {}", e.getMessage());
        }

        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        logger.info("페이로드: {}", payload);
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            Message chatMessage = new Message(jsonNode.get("payload").asText(), session.getId());
            messageRepository.save(chatMessage);
            logger.info("메시지 저장됨: {}", chatMessage);
            
            // Create a map with the same format as afterConnectionEstablished
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("messages", List.of(chatMessage));
            
            String jsonMessages = objectMapper.writeValueAsString(messageData);
            TextMessage broadcastMessage = new TextMessage(jsonMessages);
            
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
        } catch (JsonProcessingException e) {
            logger.error("JSON 파싱 실패: {}", e.getMessage());
        }
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

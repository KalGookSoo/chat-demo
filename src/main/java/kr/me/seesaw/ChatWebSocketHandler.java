package kr.me.seesaw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.entity.Message;
import kr.me.seesaw.entity.MessageType;
import kr.me.seesaw.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        sessions.put(session.getId(), session);
        if (session.getAttributes().get("authentication") instanceof Authentication authentication) {
            URI uri = session.getUri();
            if (uri == null) {
                session.close(CloseStatus.BAD_DATA.withReason("URI가 없습니다."));
                return;
            }

            // QueryParams로 채팅방 식별
            String chatRoomId = UriComponentsBuilder.fromUri(uri)
                    .build()
                    .getQueryParams()
                    .getFirst("chatRoomId");

            // 세션에서 사용자 정보 식별
            String content = "선수 입장: " + authentication.getName();

            // 알림
            Message message = new Message(content, authentication.getName(), chatRoomId, MessageType.NOTIFICATION, "text/plain");
//            messageRepository.save(message);
            try {
                String jsonMessages = objectMapper.writeValueAsString(Map.of("message", message));
                TextMessage textMessage = new TextMessage(jsonMessages);
                session.sendMessage(textMessage);
            } catch (JsonProcessingException e) {
                logger.error("메시지 JSON 변환 실패: {}", e.getMessage());
            } catch (IOException e) {
                logger.error("메시지 전송 실패: {}", e.getMessage());
            }

            // TODO 서비스 워커 알림
            return;
        }
        session.close(CloseStatus.NOT_ACCEPTABLE.withReason("계정 인증 실패"));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage textMessage) throws IOException {
        if (!(session.getAttributes().get("authentication") instanceof Authentication authentication)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("계정 인증 실패"));
            return;
        }
        URI uri = session.getUri();
        if (uri == null) {
            session.close(CloseStatus.BAD_DATA.withReason("URI가 없습니다."));
            return;
        }

        // QueryParams로 채팅방 식별
        String chatRoomId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("chatRoomId");

        String payload = textMessage.getPayload();
        Message message = new Message(payload, authentication.getName(), chatRoomId, MessageType.TEXT, "text/plain");
        messageRepository.save(message);

        String jsonMessages = objectMapper.writeValueAsString(Map.of("message", message));
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


        // TODO 서비스 워커 알림


        // TODO 채팅방에 메시지 전송
        // TODO 채팅방에 구독한 구독자들에게 푸시 알림
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

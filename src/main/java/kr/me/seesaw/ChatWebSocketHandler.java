package kr.me.seesaw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.entity.Message;
import kr.me.seesaw.repository.MessageRepository;
import kr.me.seesaw.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final Map<String, FileTransferSession> fileTransferSessions = new ConcurrentHashMap<>();

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    private final NotificationService notificationService;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        logger.info("선수 입장: {}", session.getId());

        PageRequest pageRequest = PageRequest.of(0, 100, Sort.Direction.DESC, "createdDate");
        Page<Message> page = messageRepository.findAll(pageRequest);

        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("page", page);

            String jsonMessages = objectMapper.writeValueAsString(messageData);
            TextMessage textMessage = new TextMessage(jsonMessages);
            session.sendMessage(textMessage);
            logger.info("메시지 일괄 전송 완료: {} 개", page.getContent().size());// page.getContent가 null인 경우가 존재하는가?
        } catch (JsonProcessingException e) {
            logger.error("메시지 JSON 변환 실패: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("메시지 전송 실패: {}", e.getMessage());
        }

        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage textMessage) {
        String payload = textMessage.getPayload();
        logger.info("페이로드: {}", payload);
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            Message message = new Message(jsonNode.get("payload").asText(), session.getId());
            messageRepository.save(message);
            logger.info("메시지 저장됨: {}", message);

            // Create a map with the same format as afterConnectionEstablished
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("messages", List.of(message));

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

            notificationService.sendPushNotifications(message);
            logger.info("푸시 알림 전송 요청됨");
        } catch (JsonProcessingException e) {
            logger.error("JSON 파싱 실패: {}", e.getMessage());
        }
    }

    @Override
    protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage binaryMessage) throws Exception {
        String sessionId = session.getId();
        FileTransferSession transferSession = fileTransferSessions.get(sessionId);

        if (transferSession == null) {
            return;
        }

        // 바이너리 데이터를 파일 스트림에 추가
        ByteBuffer buffer = binaryMessage.getPayload();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        transferSession.appendData(bytes);

        // 파일 수신 완료 확인
        if (!transferSession.isComplete()) {
            return;
        }

        logger.info("파일 수신 완료: {}", transferSession.getFileName());

        // 파일 저장 또는 처리
        saveFile(transferSession.getFileName(), transferSession.getData());

        // 응답 전송
        session.sendMessage(new TextMessage("{\"status\":\"success\",\"message\":\"파일 수신 완료\"}"));

        // 리소스 정리
        sessions.remove(sessionId);
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

    // 파일 전송 세션 클래스
    @Getter
    private static class FileTransferSession {

        private final String fileName;

        private final long fileSize;

        private final String mimeType;

        private final ByteArrayOutputStream fileData;

        public FileTransferSession(String fileName, long fileSize, String mimeType) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.mimeType = mimeType;
            this.fileData = new ByteArrayOutputStream();
        }

        public void appendData(byte[] data) throws IOException {
            fileData.write(data);
        }

        public boolean isComplete() {
            return fileData.size() >= fileSize;
        }

        public byte[] getData() {
            return fileData.toByteArray();
        }

    }

    private void saveFile(String fileName, byte[] data) throws IOException {
        Path path = Paths.get("uploads");
        Files.createDirectories(path.getParent());
        Files.write(path, data);
    }

}

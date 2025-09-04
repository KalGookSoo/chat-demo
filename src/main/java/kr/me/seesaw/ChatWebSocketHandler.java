package kr.me.seesaw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kr.me.seesaw.domain.Message;
import kr.me.seesaw.domain.MessageType;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.dto.MessageResponse;
import kr.me.seesaw.dto.SenderResponse;
import kr.me.seesaw.service.MessageService;
import kr.me.seesaw.service.UserService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MessageService messageService;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    private ChatSessionManager chatSessionManager;

    @PostConstruct
    public void init() {
        chatSessionManager = new ChatSessionManager(objectMapper);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws JsonProcessingException {
        if (!(session.getAttributes().get("authentication") instanceof Authentication authentication)) {
            logger.warn("연결 수립 중 인증 정보를 찾을 수 없습니다. sessionId={}", session.getId());
            return;
        }
        URI uri = session.getUri();
        if (uri == null) {
            logger.warn("연결 수립 중 URI가 없습니다. sessionId={}", session.getId());
            return;
        }

        // QueryParams로 채팅방 식별
        String chatRoomId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("chatRoomId");

        if (chatRoomId == null || chatRoomId.isBlank()) {
            logger.warn("연결 수립 중 chatRoomId가 없습니다. sessionId={}", session.getId());
            return;
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        // 세션 등록
        chatSessionManager.addSession(session, user.getId(), chatRoomId);

        // 알림
        String content = "선수 입장: " + user.getName();
        Message message = new Message(content, authentication.getName(), chatRoomId, MessageType.NOTIFICATION, MediaType.TEXT_PLAIN_VALUE);
        MessageResponse messageResponse = new MessageResponse(
                message.getId(),
                message.getChatRoomId(),
                message.getContent(),
                message.getType(),
                message.getMimeType(),
                message.getCreatedDate(),
                new SenderResponse(message.getId(), user.getName())
        );
        String broadcastMessage = objectMapper.writeValueAsString(Map.of("message", messageResponse));
        chatSessionManager.broadcastToRoom(chatRoomId, broadcastMessage);

        // TODO 서비스 워커 알림
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage textMessage) throws IOException {
        if (!(session.getAttributes().get("authentication") instanceof Authentication authentication)) {
            logger.warn("인증 정보 부재로 메시지를 처리할 수 없습니다. sessionId={}", session.getId());
            return;
        }
        URI uri = session.getUri();
        if (uri == null) {
            logger.warn("URI 부재로 메시지를 처리할 수 없습니다. sessionId={}", session.getId());
            return;
        }

        // QueryParams로 채팅방 식별
        String chatRoomId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("chatRoomId");

        if (chatRoomId == null || chatRoomId.isBlank()) {
            logger.warn("chatRoomId 부재로 메시지를 처리할 수 없습니다. sessionId={}", session.getId());
            return;
        }

        // 메시지 영속화
        User user = userService.getUserByUsername(authentication.getName());
        Message message = messageService.createMessage(textMessage.getPayload(), user.getId(), chatRoomId, MessageType.CHAT, MediaType.TEXT_PLAIN_VALUE);

        MessageResponse messageResponse = new MessageResponse(
                message.getId(),
                message.getChatRoomId(),
                message.getContent(),
                message.getType(),
                message.getMimeType(),
                message.getCreatedDate(),
                new SenderResponse(message.getId(), user.getName())
        );
        String broadcastMessage = objectMapper.writeValueAsString(Map.of("message", messageResponse));
        chatSessionManager.broadcastToRoom(chatRoomId, broadcastMessage);

        // TODO 서비스 워커 알림
        // TODO 채팅방에 메시지 전송
        // TODO 채팅방에 구독한 구독자들에게 푸시 알림
    }


    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) throws Exception {
        UserSession info = chatSessionManager.getSessionInfo(session.getId());
        String userId = info != null ? info.getUserId() : "unknown";
        String chatRoomId = info != null ? info.getChatRoomId() : "unknown";
        logger.error("전송 오류 - sessionId={}, userId={}, chatRoomId={}, exception={} : {}",
                session.getId(), userId, chatRoomId, exception.getClass().getSimpleName(), exception.getMessage());
        if (exception instanceof IOException) {
            // IO 오류 시 세션 정리
            chatSessionManager.removeSession(session);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        UserSession userSession = chatSessionManager.removeSession(session);
        if (userSession != null) {
            int code = status.getCode();
            if (code == CloseStatus.NO_CLOSE_FRAME.getCode()) {
                logger.warn("비정상 종료(1006/NO_CLOSE_FRAME): {}, 상태: {}", userSession, status);
            } else {
                logger.info("연결 종료: {}, 상태: {}", userSession, status);
            }

            // 알림 전송
            User user = userService.getUserById(userSession.getUserId());
            String exitMessage = "선수 퇴장: " + user.getName();
            Message message = new Message(exitMessage, userSession.getUserId(), userSession.getChatRoomId(), MessageType.NOTIFICATION, MediaType.TEXT_PLAIN_VALUE);
            MessageResponse messageResponse = new MessageResponse(
                    message.getId(),
                    message.getChatRoomId(),
                    message.getContent(),
                    message.getType(),
                    message.getMimeType(),
                    message.getCreatedDate(),
                    new SenderResponse(message.getId(), user.getName())
            );
            String broadcastMessage = objectMapper.writeValueAsString(Map.of("message", messageResponse));
            chatSessionManager.broadcastToRoom(userSession.getChatRoomId(), broadcastMessage);
        }
    }

    @RequiredArgsConstructor
    public static class ChatSessionManager {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        // 채팅방별 세션 관리: chatRoomId -> Set<WebSocketSession>
        private final Map<String, Set<WebSocketSession>> sessionSetByChatRoomId = new ConcurrentHashMap<>();

        // 세션별 사용자 정보 관리: sessionId -> UserSession
        private final Map<String, UserSession> userSessionBySessionId = new ConcurrentHashMap<>();

        // 사용자별 세션 관리: userId -> Set<sessionId> (다중 접속 지원)
        private final Map<String, Set<String>> sessionIdSetByUserId = new ConcurrentHashMap<>();

        private final ObjectMapper objectMapper;

        /**
         * 새로운 세션을 등록합니다
         */
        public void addSession(WebSocketSession session, String userId, String chatRoomId) {
            String sessionId = session.getId();

            // 1. 채팅방별 세션 관리
            sessionSetByChatRoomId.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(session);

            // 2. 세션별 사용자 정보 저장
            UserSession userInfo = new UserSession(sessionId, userId, chatRoomId);
            userSessionBySessionId.put(sessionId, userInfo);

            // 3. 사용자별 세션 관리 (다중 접속 지원)
            sessionIdSetByUserId.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);

            logger.info("세션 등록됨 - {}", userInfo);
            logger.info("채팅방 {} 현재 참가자 수: {}", chatRoomId, getRoomParticipantCount(chatRoomId));
        }

        /**
         * 세션을 제거합니다
         */
        public UserSession removeSession(WebSocketSession session) {
            String sessionId = session.getId();
            UserSession userInfo = userSessionBySessionId.remove(sessionId);

            if (userInfo != null) {
                String userId = userInfo.getUserId();
                String chatRoomId = userInfo.getChatRoomId();

                // 1. 채팅방에서 세션 제거
                Set<WebSocketSession> roomSessionSet = sessionSetByChatRoomId.get(chatRoomId);
                if (roomSessionSet != null) {
                    roomSessionSet.remove(session);
                    // 채팅방에 세션이 없으면 채팅방 정보 제거
                    if (roomSessionSet.isEmpty()) {
                        sessionSetByChatRoomId.remove(chatRoomId);
                        logger.info("빈 채팅방 제거: {}", chatRoomId);
                    }
                }

                // 2. 사용자별 세션 관리에서 제거
                Set<String> userSessionSet = sessionIdSetByUserId.get(userId);
                if (userSessionSet != null) {
                    userSessionSet.remove(sessionId);
                    // 사용자의 모든 세션이 종료되면 사용자 정보 제거
                    if (userSessionSet.isEmpty()) {
                        sessionIdSetByUserId.remove(userId);
                        logger.info("사용자의 모든 세션 종료: {}", userId);
                    }
                }

                logger.info("세션 제거됨 - {}", userInfo);
            }

            return userInfo;
        }

        /**
         * 특정 채팅방의 모든 세션에 메시지 전송
         */
        public void broadcastToRoom(String chatRoomId, String textMessage) {
            Set<WebSocketSession> sessions = sessionSetByChatRoomId.get(chatRoomId);
            if (sessions == null || sessions.isEmpty()) {
                logger.debug("채팅방에 활성 세션이 없음: {}", chatRoomId);
                return;
            }

            List<WebSocketSession> closedSessions = new ArrayList<>();

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(textMessage));
                    } catch (IOException e) {
                        logger.error("메시지 전송 실패 - 세션ID: {}, 오류: {}", session.getId(), e.getMessage());
                        closedSessions.add(session);
                    }
                } else {
                    closedSessions.add(session);
                }
            }

            // 끊어진 세션들 정리
            closedSessions.forEach(this::removeSession);
        }

        /**
         * 특정 사용자의 모든 세션에 메시지 전송 (다중 접속된 경우)
         */
        public void sendMessageToUser(String userId, Message message) {
            Set<String> sessionIds = sessionIdSetByUserId.get(userId);
            if (sessionIds == null || sessionIds.isEmpty()) {
                logger.debug("사용자의 활성 세션이 없음: {}", userId);
                return;
            }

            try {
                String jsonMessage = objectMapper.writeValueAsString(Map.of("message", message));
                TextMessage textMessage = new TextMessage(jsonMessage);

                sessionIds.forEach(sessionId -> {
                    UserSession userInfo = userSessionBySessionId.get(sessionId);
                    if (userInfo != null) {
                        Set<WebSocketSession> roomSessionSet = sessionSetByChatRoomId.get(userInfo.getChatRoomId());
                        if (roomSessionSet != null) {
                            roomSessionSet.stream()
                                    .filter(session -> session.getId().equals(sessionId))
                                    .filter(WebSocketSession::isOpen)
                                    .forEach(session -> {
                                        try {
                                            session.sendMessage(textMessage);
                                        } catch (IOException e) {
                                            logger.error("사용자별 메시지 전송 실패 - 사용자: {}, 세션ID: {}, 오류: {}",
                                                    userId, sessionId, e.getMessage());
                                            // 전송 실패 세션 정리
                                            removeSession(session);
                                        }
                                    });
                        }
                    }
                });
            } catch (JsonProcessingException e) {
                logger.error("메시지 JSON 변환 실패: {}", e.getMessage());
            }
        }

        /**
         * 세션 정보 조회
         */
        public UserSession getSessionInfo(String sessionId) {
            return userSessionBySessionId.get(sessionId);
        }

        /**
         * 특정 채팅방의 참가자 수 조회
         */
        public int getRoomParticipantCount(String chatRoomId) {
            Set<WebSocketSession> sessions = sessionSetByChatRoomId.get(chatRoomId);
            return sessions != null ? (int) sessions.stream().filter(WebSocketSession::isOpen).count() : 0;
        }

        /**
         * 특정 채팅방의 활성 사용자 목록 조회
         */
        public Set<String> getRoomActiveUsers(String chatRoomId) {
            Set<WebSocketSession> sessions = sessionSetByChatRoomId.get(chatRoomId);
            if (sessions == null) {
                return Collections.emptySet();
            }

            return sessions.stream()
                    .filter(WebSocketSession::isOpen)
                    .map(WebSocketSession::getId)
                    .map(userSessionBySessionId::get)
                    .filter(Objects::nonNull)
                    .map(UserSession::getUserId)
                    .collect(Collectors.toSet());
        }

        /**
         * 전체 채팅방 상태 정보 조회
         */
        public Map<String, Object> getAllRoomsStatus() {
            Map<String, Object> status = new ConcurrentHashMap<>();

            sessionSetByChatRoomId.forEach((chatRoomId, sessions) -> {
                Set<String> activeUsers = getRoomActiveUsers(chatRoomId);
                status.put(chatRoomId, Map.of(
                        "participantCount", getRoomParticipantCount(chatRoomId),
                        "activeUsers", activeUsers
                ));
            });

            return status;
        }

        /**
         * 특정 사용자의 세션 수 조회
         */
        public int getUserSessionCount(String userId) {
            Set<String> sessionIds = sessionIdSetByUserId.get(userId);
            return sessionIds != null ? sessionIds.size() : 0;
        }

        /**
         * 사용자가 특정 채팅방에 참여 중인지 확인
         */
        public boolean isUserInRoom(String userId, String chatRoomId) {
            Set<String> sessionIds = sessionIdSetByUserId.get(userId);
            if (sessionIds == null || sessionIds.isEmpty()) {
                return false;
            }

            return sessionIds.stream()
                    .map(userSessionBySessionId::get)
                    .filter(Objects::nonNull)
                    .anyMatch(info -> info.getChatRoomId().equals(chatRoomId));
        }

        /**
         * 끊어진 세션들을 정리합니다
         */
        public void cleanupClosedSessions() {
            List<WebSocketSession> closedSessions = new ArrayList<>();

            sessionSetByChatRoomId.values().forEach(sessions -> {
                sessions.forEach(session -> {
                    if (!session.isOpen()) {
                        closedSessions.add(session);
                    }
                });
            });

            closedSessions.forEach(this::removeSession);

            logger.info("끊어진 세션 {} 개 정리 완료", closedSessions.size());
        }

        /**
         * 전체 통계 정보 조회
         */
        public Map<String, Object> getStatistics() {
            return Map.of(
                    "totalRooms", sessionSetByChatRoomId.size(),
                    "totalSessions", userSessionBySessionId.size(),
                    "totalUsers", sessionIdSetByUserId.size(),
                    "roomDetails", getAllRoomsStatus()
            );
        }

    }

    /**
     * 세션 정보를 담는 내부 클래스
     */
    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class UserSession {
        private final String userId;
        private final String chatRoomId;
        private final long connectionTime;
        private final String sessionId;

        public UserSession(String sessionId, String userId, String chatRoomId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.chatRoomId = chatRoomId;
            this.connectionTime = System.currentTimeMillis();
        }

    }

}

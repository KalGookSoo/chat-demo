package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.domain.Message;
import kr.me.seesaw.domain.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultMessageService implements MessageService {
    private final EntityManager entityManager;

    @Override
    public Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String contentType) {
        Message message = new Message(content, senderId, chatRoomId, MessageType.TEXT, "text/plain");
        entityManager.persist(message);
        return message;
    }
}

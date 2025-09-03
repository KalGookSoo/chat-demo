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
    public Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String mimeType) {
        Message message = new Message(content, senderId, chatRoomId, type, mimeType);
        entityManager.persist(message);
        return message;
    }
}

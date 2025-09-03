package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.domain.Message;
import kr.me.seesaw.domain.MessageType;
import kr.me.seesaw.dto.MessageResponse;
import kr.me.seesaw.repository.ChatRoomMemberRepository;
import kr.me.seesaw.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultMessageService implements MessageService {
    private final EntityManager entityManager;

    private final MessageRepository messageRepository;

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String mimeType) {
        Message message = new Message(content, senderId, chatRoomId, type, mimeType);
        entityManager.persist(message);
        return message;
    }

    @Override
    public Page<MessageResponse> getMessagesByChatRoomId(String chatRoomId) {
        Sort sort = Sort.by(Sort.Order.asc("createdDate"));
        PageRequest pageRequest = PageRequest.of(0, 30, sort);
        Page<Message> page = messageRepository.findAllByChatRoomId(chatRoomId, pageRequest);
        List<MessageResponse> messageResponses = page.getContent()
                .stream()
                .map(message ->
                        new MessageResponse(
                                message.getId(),
                                message.getSenderId(),
                                message.getChatRoomId(),
                                message.getContent(),
                                message.getType(),
                                message.getMimeType(),
                                message.getCreatedDate()
                        )
                ).toList();
        return new PageImpl<>(messageResponses, page.getPageable(), page.getTotalElements());
    }

    @Override
    public boolean isMember(String chatRoomId, String userId) {
        return chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoomId, userId).isPresent();
    }
}

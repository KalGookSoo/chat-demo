package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.MessageResponse;
import kr.me.seesaw.domain.dto.SenderResponse;
import kr.me.seesaw.domain.entity.BaseEntity;
import kr.me.seesaw.domain.entity.ChatRoom;
import kr.me.seesaw.domain.entity.Message;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.domain.vo.MessageType;
import kr.me.seesaw.repository.ChatRoomRepository;
import kr.me.seesaw.repository.MessageRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service("messageService")
public class DefaultMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    @Override
    public Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String mimeType) {
        log.info("메시지 생성을 시작합니다. content: {}, senderId: {}, chatRoomId: {}, type: {}, mimeType: {}", content, senderId, chatRoomId, type, mimeType);
        User sender = userRepository.findById(senderId).orElseThrow();
        ChatRoom chatRoom = chatRoomRepository.getReferenceById(chatRoomId);

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setChatRoom(chatRoom);
        message.setType(type);
        message.setMimeType(mimeType);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Page<MessageResponse> getMessagesByChatRoomId(String chatRoomId, int pageNumber, int pageSize) {
        log.debug("채팅방 ID {}에 대한 메시지 조회를 시작합니다. 페이지 번호: {}, 페이지 크기: {}", chatRoomId, pageNumber, pageSize);

        Sort sort = Sort.by(Sort.Order.desc("createdDate"));
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<Message> page = messageRepository.findAllByChatRoomId(chatRoomId, pageRequest);
        Collection<String> userIds = page.getContent()
                .stream()
                .map(Message::getSenderId)
                .toList();
        Map<String, User> users = userRepository.findAllByIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        List<MessageResponse> messageResponses = page.getContent()
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedDate))
                .map(message ->
                        {
                            SenderResponse sender = SenderResponse.builder()
                                    .id(message.getSenderId())
                                    .name(users.get(message.getSenderId()).getName())
                                    .build();
                            return MessageResponse.builder()
                                    .id(message.getId())
                                    .chatRoomId(message.getChatRoomId())
                                    .content(message.getContent())
                                    .type(message.getType())
                                    .mimeType(message.getMimeType())
                                    .createdDate(message.getCreatedDate())
                                    .sender(sender)
                                    .build();
                        }
                ).toList();
        return new PageImpl<>(messageResponses, page.getPageable(), page.getTotalElements());
    }

}

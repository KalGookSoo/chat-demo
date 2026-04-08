package kr.me.seesaw.service;

import kr.me.seesaw.domain.entity.BaseEntity;
import kr.me.seesaw.domain.entity.Message;
import kr.me.seesaw.domain.vo.MessageType;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.domain.dto.MessageResponse;
import kr.me.seesaw.domain.dto.SenderResponse;
import kr.me.seesaw.repository.ChatRoomMemberRepository;
import kr.me.seesaw.repository.MessageRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Transactional
@Service("messageService")
public class DefaultMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final UserRepository userRepository;

    @Override
    public Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String mimeType) {
        Message message = new Message();
        message.setContent(content);
        message.setSenderId(senderId);
        message.setChatRoomId(chatRoomId);
        message.setType(type);
        message.setMimeType(mimeType);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Page<MessageResponse> getMessagesByChatRoomId(String chatRoomId, int pageNumber, int pageSize) {
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
                        new MessageResponse(
                                message.getId(),
                                message.getChatRoomId(),
                                message.getContent(),
                                message.getType(),
                                message.getMimeType(),
                                message.getCreatedDate(),
                                new SenderResponse(message.getSenderId(), users.get(message.getSenderId()).getName())
                        )
                ).toList();
        return new PageImpl<>(messageResponses, page.getPageable(), page.getTotalElements());
    }

}

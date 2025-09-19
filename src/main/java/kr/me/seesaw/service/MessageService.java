package kr.me.seesaw.service;

import kr.me.seesaw.domain.Message;
import kr.me.seesaw.domain.MessageType;
import kr.me.seesaw.dto.MessageResponse;
import org.springframework.data.domain.Page;

public interface MessageService {

    Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String contentType);

    Page<MessageResponse> getMessagesByChatRoomId(String chatRoomId, int pageNumber, int pageSize);

    boolean isMember(String chatRoomId, String userId);

}

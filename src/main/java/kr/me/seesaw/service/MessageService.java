package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.MessageResponse;
import kr.me.seesaw.domain.vo.MessageType;
import org.springframework.data.domain.Page;

public interface MessageService {

    MessageResponse createMessage(String content, String senderId, String chatRoomId, MessageType type, String contentType);

    Page<MessageResponse> getMessagesByChatRoomId(String chatRoomId, int pageNumber, int pageSize);

}

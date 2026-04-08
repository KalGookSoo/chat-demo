package kr.me.seesaw.service;

import kr.me.seesaw.domain.entity.Message;
import kr.me.seesaw.domain.vo.MessageType;
import kr.me.seesaw.domain.dto.MessageResponse;
import org.springframework.data.domain.Page;

public interface MessageService {

    Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String contentType);

    Page<MessageResponse> getMessagesByChatRoomId(String chatRoomId, int pageNumber, int pageSize);

}

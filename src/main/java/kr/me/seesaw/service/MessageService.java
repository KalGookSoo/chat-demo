package kr.me.seesaw.service;

import kr.me.seesaw.domain.Message;
import kr.me.seesaw.domain.MessageType;

public interface MessageService {
    Message createMessage(String content, String senderId, String chatRoomId, MessageType type, String contentType);
}

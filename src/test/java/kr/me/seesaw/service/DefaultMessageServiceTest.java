package kr.me.seesaw.service;

import kr.me.seesaw.domain.entity.Message;
import kr.me.seesaw.domain.vo.MessageType;
import kr.me.seesaw.repository.ChatRoomMemberRepository;
import kr.me.seesaw.repository.MessageRepository;
import kr.me.seesaw.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles({"test"})
@DataJpaTest
class DefaultMessageServiceTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Autowired
    private UserRepository userRepository;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new DefaultMessageService(messageRepository, chatRoomMemberRepository, userRepository);
    }

    @Test
    @DisplayName("메시지 저장 시 메지리를 반환해야 한다")
    void createMessageShouldReturnMessage() {
        // given
        String content = "content";
        String senderId = UUID.randomUUID().toString();
        String chatRoomId = "chatRoomId";
        MessageType type = MessageType.CHAT;
        String mimeType = MediaType.TEXT_PLAIN_VALUE;

        // when
        Message message = messageService.createMessage(content, senderId, chatRoomId, type, mimeType);
        entityManager.flush();

        // then
        Assertions.assertEquals(content, message.getContent());
    }
}
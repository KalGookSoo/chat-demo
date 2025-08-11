package kr.me.seesaw;

import kr.me.seesaw.entity.Message;
import kr.me.seesaw.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class MessagePersistenceTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void testSaveAndRetrieveMessage() {
        // Create a test message
        Message message = new Message("Test message content", "test-sender-id");
        
        // Save the message
        Message savedMessage = messageRepository.save(message);
        
        // Verify the message was saved with an ID
        assertNotNull(savedMessage.getId());
        System.out.println("[DEBUG_LOG] Saved message: " + savedMessage);
        
        // Retrieve the message by ID
        Message retrievedMessage = messageRepository.findById(savedMessage.getId()).orElse(null);
        assertNotNull(retrievedMessage);
        assertEquals("Test message content", retrievedMessage.getContent());
        assertEquals("test-sender-id", retrievedMessage.getSenderId());
        
        // Test findBySenderId
        List<Message> senderMessages = messageRepository.findBySenderId("test-sender-id");
        assertEquals(1, senderMessages.size());
        
        // Test findTop100ByOrderByTimestampDesc
        List<Message> recentMessages = messageRepository.findTop100ByOrderByTimestampDesc();
        assertEquals(1, recentMessages.size());
        assertEquals(savedMessage.getId(), recentMessages.get(0).getId());
    }
}
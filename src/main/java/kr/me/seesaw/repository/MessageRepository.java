package kr.me.seesaw.repository;

import kr.me.seesaw.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find messages by senderId
    List<Message> findBySenderId(String senderId);
    
    // Find latest messages with limit
    List<Message> findTop100ByOrderByTimestampDesc();
}
package kr.me.seesaw.repository;

import kr.me.seesaw.entity.Message;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends Repository<Message, Long> {

    Message save(Message message);

    Optional<Message> findById(Long id);

    List<Message> findBySenderId(String senderId);
    
    List<Message> findTop100ByOrderByTimestampDesc();

}
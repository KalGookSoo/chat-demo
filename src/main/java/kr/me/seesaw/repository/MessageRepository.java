package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends Repository<Message, String> {

    void save(Message message);

    Optional<Message> findById(String id);

    List<Message> findBySenderId(String senderId);

    Page<Message> findAll(Pageable pageable);

    Page<Message> findAllByChatRoomId(String chatRoomId, Pageable pageable);

}
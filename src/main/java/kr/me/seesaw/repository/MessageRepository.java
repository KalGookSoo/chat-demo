package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface MessageRepository extends Repository<Message, String> {
    Page<Message> findAllByChatRoomId(String chatRoomId, Pageable pageable);
}
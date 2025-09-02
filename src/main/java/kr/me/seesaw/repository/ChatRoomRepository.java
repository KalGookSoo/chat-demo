package kr.me.seesaw.repository;

import kr.me.seesaw.domain.ChatRoom;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends Repository<ChatRoom, String> {
    void save(ChatRoom chatRoom);

    Optional<ChatRoom> findById(String id);

    void deleteById(String id);

    List<ChatRoom> findAll();
}

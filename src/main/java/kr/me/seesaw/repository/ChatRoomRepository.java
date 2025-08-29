package kr.me.seesaw.repository;

import kr.me.seesaw.entity.ChatRoom;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ChatRoomRepository extends Repository<ChatRoom, String> {

    void save(ChatRoom chatRoom);

    Optional<ChatRoom> findById(String id);

    void deleteById(String id);

}

package kr.me.seesaw.repository;

import kr.me.seesaw.domain.ChatRoom;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ChatRoomRepository extends Repository<ChatRoom, String> {

    List<ChatRoom> findAll();

    void save(ChatRoom chatRoom);

    ChatRoom getReferenceById(String id);

}

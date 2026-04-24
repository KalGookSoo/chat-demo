package kr.me.seesaw.repository;

import kr.me.seesaw.domain.entity.ChatRoom;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends Repository<ChatRoom, String> {

    List<ChatRoom> findAll();

    ChatRoom save(ChatRoom chatRoom);

    ChatRoom getReferenceById(String id);

    Optional<ChatRoom> findById(String id);

    List<ChatRoom> findAllByIdIn(Collection<String> chatRoomIds);

}

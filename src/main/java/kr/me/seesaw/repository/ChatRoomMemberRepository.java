package kr.me.seesaw.repository;

import kr.me.seesaw.entity.ChatRoomMember;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ChatRoomMemberRepository extends Repository<ChatRoomMember, String> {

    void save(ChatRoomMember chatRoomMember);

    List<ChatRoomMember> findAllByChatRoomId(String chatRoomId);

}

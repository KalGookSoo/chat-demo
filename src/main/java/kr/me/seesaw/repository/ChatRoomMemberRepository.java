package kr.me.seesaw.repository;

import kr.me.seesaw.domain.ChatRoomMember;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends Repository<ChatRoomMember, String> {

    Optional<ChatRoomMember> findByChatRoomIdAndUserId(String chatRoomId, String userId);

    List<ChatRoomMember> findAllByUserId(String userId);

    void save(ChatRoomMember chatRoomMember);

}

package kr.me.seesaw.repository;

import kr.me.seesaw.domain.entity.ChatRoomMember;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends Repository<ChatRoomMember, String> {

    Optional<ChatRoomMember> findByChatRoomIdAndUserId(String chatRoomId, String userId);

    List<ChatRoomMember> findAllByUserId(String userId);

    List<ChatRoomMember> findAllByChatRoomId(String chatRoomId);

    List<ChatRoomMember> findAllByChatRoomIdIn(Collection<String> chatRoomIds);

    void save(ChatRoomMember chatRoomMember);

    void delete(ChatRoomMember chatRoomMember);

}

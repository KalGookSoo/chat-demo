package kr.me.seesaw.repository;

import kr.me.seesaw.domain.entity.Friend;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends Repository<Friend, String> {

    Friend save(Friend friend);

    void delete(Friend friend);

    boolean existsByUserIdAndFriendId(String userId, String friendId);

    Optional<Friend> findByUserIdAndFriendId(String userId, String friendId);

    List<Friend> findByUserIdOrFriendId(String userId, String friendId);

}

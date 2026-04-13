package kr.me.seesaw.repository;

import kr.me.seesaw.domain.entity.Friend;
import kr.me.seesaw.domain.vo.FriendStatus;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends Repository<Friend, String> {

    Friend save(Friend friend);

    void delete(Friend friend);

    boolean existsByUserIdAndFriendId(String userId, String friendId);

    Optional<Friend> findByUserIdAndFriendId(String userId, String friendId);

    /**
     * 내가 추가한 친구이거나, 나를 추가한 친구인 경우 (수락된 관계)
     * 단방향 기반이므로 수락된 경우만 조회
     */
    List<Friend> findByUserIdAndStatusOrFriendIdAndStatus(String userId, FriendStatus status1, String friendId, FriendStatus status2);

}

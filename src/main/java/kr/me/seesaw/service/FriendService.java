package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.FriendResponse;

import java.util.List;

public interface FriendService {

    void requestFriend(String friendId);

    void acceptFriend(String friendId);

    void removeFriend(String friendId);

    List<FriendResponse> getFriends();

    List<FriendResponse> getPendingRequests();

}

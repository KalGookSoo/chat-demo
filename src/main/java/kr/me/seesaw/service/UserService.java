package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.UserResponse;
import kr.me.seesaw.domain.dto.UserSearch;
import kr.me.seesaw.domain.entity.User;

import java.util.List;

public interface UserService {

    void createUser(String username, String password, String name);

    User getUserByUsername(String username);

    User getUserById(String id);

    UserResponse getUser(String id);

    List<UserResponse> searchUsers(UserSearch search);

    void changePassword(String userId, String newPassword);

    void updateProfile(String userId, String name);

}

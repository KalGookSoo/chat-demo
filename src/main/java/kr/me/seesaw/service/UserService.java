package kr.me.seesaw.service;

import kr.me.seesaw.domain.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    void createDemoUsers();

    void createUser(String username, String password, String name);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    List<User> getUsersById(Collection<String> userIds);
}

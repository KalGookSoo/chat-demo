package kr.me.seesaw.service;

import kr.me.seesaw.domain.User;

public interface UserService {
    void createDemoUsers();

    void createUser(String username, String password, String name);

    User getUserByUsername(String username);
}

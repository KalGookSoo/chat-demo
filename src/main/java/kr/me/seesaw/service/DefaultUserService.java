package kr.me.seesaw.service;

import kr.me.seesaw.domain.entity.Role;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void createDemoUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User user = User.create("admin", passwordEncoder.encode("1234"), "최고관리자");
            Role role = new Role("ROLE_ADMIN", "최고관리자");
            user.addRole(role);
            userRepository.save(user);
        }
        if (userRepository.findByUsername("manager1").isEmpty()) {
            User user = User.create("manager1", passwordEncoder.encode("1234"), "관리자1");
            Role role = new Role("ROLE_MANAGER", "관리자");
            user.addRole(role);
            userRepository.save(user);
        }
        if (userRepository.findByUsername("testuser1").isEmpty()) {
            User user = User.create("testuser1", passwordEncoder.encode("1234"), "테스트유저1");
            Role role = new Role("ROLE_USER", "일반사용자");
            user.addRole(role);
            userRepository.save(user);
        }
        if (userRepository.findByUsername("testuser2").isEmpty()) {
            User user = User.create("testuser2", passwordEncoder.encode("1234"), "테스트유저2");
            Role role = new Role("ROLE_USER", "일반사용자");
            user.addRole(role);
            userRepository.save(user);
        }
    }

    @Override
    public void createUser(String username, String password, String name) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.create(username, encodedPassword, name);
        Role role = new Role("ROLE_USER", "일반사용자");
        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. username: " + username));
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. id: " + id));
    }

}

package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.domain.Role;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultUserService implements UserService {
    private final EntityManager entityManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void createDemoUsers() {
        User user1 = User.create("tester1", passwordEncoder.encode("1234"), "테스터1");
        Role role1 = new Role("ROLE_CLIENT", "의뢰인");
        user1.addRole(role1);
        entityManager.persist(user1);

        User user2 = User.create("tester2", passwordEncoder.encode("1234"), "테스터2");
        Role role2 = new Role("ROLE_LAWYER", "변호사");
        user2.addRole(role2);
        entityManager.persist(user2);
    }

    @Override
    public void createUser(String username, String password, String name) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.create(username, encodedPassword, name);
        Role role = new Role("ROLE_CLIENT", "의뢰인");
        user.addRole(role);
        entityManager.persist(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. username: " + username));
    }

    @Override
    public User getUserById(String id) {
        return Optional.ofNullable(entityManager.find(User.class, id))
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. id: " + id));
    }
}

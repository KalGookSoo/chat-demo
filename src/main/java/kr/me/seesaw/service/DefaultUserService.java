package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.UserResponse;
import kr.me.seesaw.domain.dto.UserSearch;
import kr.me.seesaw.domain.entity.Role;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. id: " + id));

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponse.from(user)
                .roles(roleNames)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> searchUsers(UserSearch search) {
        List<User> users;
        boolean hasUsername = StringUtils.hasText(search.username());
        boolean hasName = StringUtils.hasText(search.name());

        if (hasUsername && hasName) {
            users = userRepository.findAllByUsernameContainingOrNameContaining(search.username(), search.name());
        } else if (hasUsername) {
            users = userRepository.findAllByUsernameContaining(search.username());
        } else if (hasName) {
            users = userRepository.findAllByNameContaining(search.name());
        } else {
            return List.of();
        }

        return users.stream()
                .map(UserResponse::from)
                .map(UserResponse.UserResponseBuilder::build)
                .toList();
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        User user = userRepository.getReferenceById(userId);
        user.changePassword(passwordEncoder.encode(newPassword));
    }

}

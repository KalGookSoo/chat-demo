package kr.me.seesaw;

import kr.me.seesaw.domain.entity.Role;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.ChatRoomRepository;
import kr.me.seesaw.repository.UserRepository;
import kr.me.seesaw.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DemoDataLoader {

    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRoomService chatRoomService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
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

        // AppInitializer에서 사용하던 tester1, tester2도 추가 (만약 없다면)
        if (userRepository.findByUsername("tester1").isEmpty()) {
            User user = User.create("tester1", passwordEncoder.encode("1234"), "테스터1");
            Role role = new Role("ROLE_USER", "일반사용자");
            user.addRole(role);
            userRepository.save(user);
        }
        if (userRepository.findByUsername("tester2").isEmpty()) {
            User user = User.create("tester2", passwordEncoder.encode("1234"), "테스터2");
            Role role = new Role("ROLE_USER", "일반사용자");
            user.addRole(role);
            userRepository.save(user);
        }
    }

    @Transactional
    public void createDemoChatRooms() {
        if (chatRoomRepository.findAll().isEmpty()) {
            chatRoomService.createChatRoom("채팅방1");
            chatRoomService.createChatRoom("채팅방2");
        }
    }

}

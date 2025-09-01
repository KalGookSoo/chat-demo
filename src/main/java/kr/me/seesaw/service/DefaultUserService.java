package kr.me.seesaw.service;

import kr.me.seesaw.entity.Role;
import kr.me.seesaw.entity.User;
import kr.me.seesaw.entity.UserRoleMapping;
import kr.me.seesaw.repository.RoleRepository;
import kr.me.seesaw.repository.UserRepository;
import kr.me.seesaw.repository.UserRoleMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final UserRoleMappingRepository userRoleMappingRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void createDemoUsers() {
        User user1 = User.create("tester1", passwordEncoder.encode("1234"), "테스터1");
        User user2 = User.create("tester2", passwordEncoder.encode("1234"), "테스터2");
        userRepository.save(user1);
        userRepository.save(user2);
        Role role1 = new Role("ROLE_CLIENT", "의뢰인");
        Role role2 = new Role("ROLE_LAWYER", "변호사");
        roleRepository.save(role1);
        roleRepository.save(role2);
        UserRoleMapping userRoleMapping1 = new UserRoleMapping(user1.getId(), role1.getId());
        UserRoleMapping userRoleMapping2 = new UserRoleMapping(user2.getId(), role2.getId());
        userRoleMappingRepository.save(userRoleMapping1);
        userRoleMappingRepository.save(userRoleMapping2);
    }

    @Override
    public void createUser(String username, String password, String name) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.create(username, encodedPassword, name);
        userRepository.save(user);
        Role role = new Role("ROLE_CLIENT", "의뢰인");
        roleRepository.save(role);
        UserRoleMapping userRoleMapping = new UserRoleMapping(user.getId(), role.getId());
        userRoleMappingRepository.save(userRoleMapping);
    }

}

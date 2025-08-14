package kr.me.seesaw;

import kr.me.seesaw.entity.User;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AppInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User customer = new User("testUser1", "1234", User.Role.CUSTOMER);
        User lawyer = new User("testLawyer", "1234", User.Role.LAWYER);
        User admin = new User("admin", "1234", User.Role.ADMIN);
        userRepository.save(customer);
        userRepository.save(lawyer);
        userRepository.save(admin);
    }

}

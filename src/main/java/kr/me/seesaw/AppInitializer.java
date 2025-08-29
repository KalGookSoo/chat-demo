package kr.me.seesaw;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AppInitializer implements CommandLineRunner {

//    private final UserService userService;

    @Override
    public void run(String... args) {
//        userService.createDemoUsers();
    }

}

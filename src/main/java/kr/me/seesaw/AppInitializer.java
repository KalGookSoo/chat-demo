package kr.me.seesaw;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AppInitializer implements CommandLineRunner {
//    private final UserService userService;

//    private final ChatRoomService chatRoomService;

    @Override
    public void run(String... args) {
//        userService.createDemoUsers();
//        User tester1 = userService.getUserByUsername("tester1");
//        User tester2 = userService.getUserByUsername("tester2");
//        chatRoomService.createDemoChatRooms();
//        chatRoomService.getAllChatRooms().forEach(chatRoom -> {
//            chatRoomService.addMember(chatRoom.getId(), tester1.getId());
//            chatRoomService.addMember(chatRoom.getId(), tester2.getId());
//        });
    }
}

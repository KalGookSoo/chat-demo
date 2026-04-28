package kr.me.seesaw;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.domain.entity.*;
import kr.me.seesaw.domain.vo.FriendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

/**
 * 테스트 전용 데이터 초기화 구성 클래스.
 * - test 프로필에서만 활성화됩니다.
 * - 필요 시 테스트 클래스에서 @Import(TestDataInitializerConfig.class)로 가져와 사용할 수 있습니다.
 */
@Profile("test")
@TestConfiguration
public class TestDataInitializerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataInitializerConfig.class);

    @Bean
    public ApplicationRunner testDataInitializer(EntityManager entityManager) {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) {
                LOGGER.info("테스트 데이터 초기화 시작");

                // 기본 역할 생성
                Role userRole = new Role("ROLE_USER", "일반 사용자");
                entityManager.persist(userRole);

                // 테스트 유저 1 (생성자용)
                User user1 = User.create("user1", "pass1", "사용자1");
                user1.addRole(userRole);
                entityManager.persist(user1);

                // 테스트 유저 2 (친구용)
                User user2 = User.create("user2", "pass2", "사용자2");
                user2.addRole(userRole);
                entityManager.persist(user2);

                // 테스트 유저 3 (친구 아닌 사람)
                User user3 = User.create("user3", "pass3", "사용자3");
                user3.addRole(userRole);
                entityManager.persist(user3);

                // 친구 관계 설정 (user1 <-> user2)
                Friend friend = Friend.builder()
                        .userId(user1.getId())
                        .friendId(user2.getId())
                        .status(FriendStatus.ACCEPTED)
                        .build();
                entityManager.persist(friend);

                // PushDevice 초기 데이터 (user1용 Web Push)
                PushDevice webDevice = PushDevice.builder()
                        .user(user1)
                        .provider(PushProvider.WEB_PUSH)
                        .endpoint("https://fcm.googleapis.com/fcm/send/fake-endpoint")
                        .p256dh("fake-p256dh")
                        .auth("fake-auth")
                        .userAgent("Mozilla/5.0")
                        .deviceName("Chrome Desktop")
                        .active(true)
                        .build();
                entityManager.persist(webDevice);

                // PushDevice 초기 데이터 (user1용 Expo Push)
                PushDevice expoDevice = PushDevice.builder()
                        .user(user1)
                        .provider(PushProvider.EXPO)
                        .pushToken("ExponentPushToken[fake-token]")
                        .userAgent("iOS")
                        .deviceName("iPhone 15")
                        .active(true)
                        .build();
                entityManager.persist(expoDevice);

                LOGGER.info("테스트 데이터 초기화 완료");
            }
        };
    }

}

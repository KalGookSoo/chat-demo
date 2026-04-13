package kr.me.seesaw;

import jakarta.persistence.EntityManager;
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
            }
        };
    }

}

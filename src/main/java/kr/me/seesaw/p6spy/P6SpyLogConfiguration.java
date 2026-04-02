package kr.me.seesaw.p6spy;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * P6Spy 로그 설정.
 */
@Configuration
public class P6SpyLogConfiguration implements InitializingBean {

    @Value("${decorator.datasource.p6spy.multiline:false}")
    private boolean multiline;

    /**
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        P6SpyCustomFormatter.setMultiline(multiline);
    }

}

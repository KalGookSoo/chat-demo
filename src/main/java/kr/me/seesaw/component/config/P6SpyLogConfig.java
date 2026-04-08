package kr.me.seesaw.component.config;

import kr.me.seesaw.component.p6spy.P6SpyCustomFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * P6Spy 로그 설정.
 */
@Configuration
public class P6SpyLogConfig implements InitializingBean {

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

package kr.me.seesaw.component.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kr.me.seesaw")
public class ApplicationProperties {

    private String filepath;

    private Notification notification;

}

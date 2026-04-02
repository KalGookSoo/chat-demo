package kr.me.seesaw.component.properties;

import org.springframework.boot.context.properties.bind.DefaultValue;

public record Notification(@DefaultValue("5") int corePoolSize, @DefaultValue("10") int maxPoolSize, @DefaultValue("25") int queueCapacity) {

}

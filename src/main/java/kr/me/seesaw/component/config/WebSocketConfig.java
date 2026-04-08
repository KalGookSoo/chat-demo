package kr.me.seesaw.component.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.me.seesaw.ChatWebSocketHandler;
import kr.me.seesaw.repository.MessageRepository;
import kr.me.seesaw.service.MessageService;
import kr.me.seesaw.service.UserService;
import kr.me.seesaw.web.interceptor.ChatHandshakeInterceptor;
import kr.me.seesaw.web.interceptor.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageService messageService;

    private final UserService userService;

    private final ChatHandshakeInterceptor chatHandshakeInterceptor;

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    private final MessageRepository messageRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(objectMapper()), "/chat")
                .addInterceptors(chatHandshakeInterceptor, jwtHandshakeInterceptor)
                .setAllowedOrigins("*");
    }

    @Bean
    public ChatWebSocketHandler chatWebSocketHandler(ObjectMapper objectMapper) {
        return new ChatWebSocketHandler(messageService, userService, objectMapper, messageRepository);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

}

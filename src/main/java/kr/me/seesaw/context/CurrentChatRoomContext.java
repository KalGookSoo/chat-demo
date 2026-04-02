package kr.me.seesaw.context;

import kr.me.seesaw.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequiredArgsConstructor
@RequestScope(proxyMode = ScopedProxyMode.INTERFACES)
@Component("chatRoomContext")
public class CurrentChatRoomContext implements ChatRoomContext {

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public boolean isMember(String chatRoomId, String userId) {
        return chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoomId, userId).isPresent();
    }

}

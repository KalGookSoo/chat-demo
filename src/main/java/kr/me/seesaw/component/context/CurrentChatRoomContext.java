package kr.me.seesaw.component.context;

import kr.me.seesaw.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestScope(proxyMode = ScopedProxyMode.INTERFACES)
@Component("chatRoomContext")
public class CurrentChatRoomContext implements ChatRoomContext {

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final Map<MembershipKey, Boolean> membershipCache = new HashMap<>();

    @Override
    public boolean isMember(String chatRoomId, String userId) {
        MembershipKey key = new MembershipKey(chatRoomId, userId);
        return membershipCache.computeIfAbsent(key,
                unused -> chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoomId, userId).isPresent());
    }

    private record MembershipKey(String chatRoomId, String userId) {

    }

}

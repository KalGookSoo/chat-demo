package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.PushSubscriptionRequest;
import kr.me.seesaw.domain.dto.PushSubscriptionResponse;
import kr.me.seesaw.domain.entity.PushSubscription;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.PushSubscriptionRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultPushSubscriptionService implements PushSubscriptionService {

    private final PushSubscriptionRepository pushSubscriptionRepository;

    private final UserRepository userRepository;

    @Override
    public PushSubscriptionResponse subscribe(String userId, PushSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. id: " + userId));

        PushSubscription subscription = pushSubscriptionRepository.findByEndpoint(request.getEndpoint())
                .map(existing -> {
                    existing.updateUser(user);
                    existing.updateSubscription(
                            request.getP256dh(),
                            request.getAuth(),
                            request.getUserAgent(),
                            request.getDeviceName()
                    );
                    return existing;
                })
                .orElseGet(() -> PushSubscription.builder()
                        .user(user)
                        .endpoint(request.getEndpoint())
                        .p256dh(request.getP256dh())
                        .auth(request.getAuth())
                        .userAgent(request.getUserAgent())
                        .deviceName(request.getDeviceName())
                        .build());

        PushSubscription saved = pushSubscriptionRepository.save(subscription);
        return PushSubscriptionResponse.from(saved);
    }

    @Override
    public void unsubscribe(String endpoint) {
        pushSubscriptionRepository.deleteByEndpoint(endpoint);
    }

}

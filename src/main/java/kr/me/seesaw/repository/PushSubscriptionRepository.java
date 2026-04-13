package kr.me.seesaw.repository;

import kr.me.seesaw.domain.entity.PushSubscription;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PushSubscriptionRepository extends Repository<PushSubscription, String> {

    PushSubscription save(PushSubscription pushSubscription);

    Optional<PushSubscription> findByEndpoint(String endpoint);

    void deleteByEndpoint(String endpoint);

    void deleteByUserId(String userId);

}

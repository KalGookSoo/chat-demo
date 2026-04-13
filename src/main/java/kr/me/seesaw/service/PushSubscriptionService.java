package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.PushSubscriptionRequest;
import kr.me.seesaw.domain.dto.PushSubscriptionResponse;

public interface PushSubscriptionService {

    PushSubscriptionResponse subscribe(String userId, PushSubscriptionRequest request);

    void unsubscribe(String endpoint);

}

package kr.me.seesaw.repository;

import kr.me.seesaw.entity.Subscriber;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface SubscriberRepository extends Repository<Subscriber, String> {

    Subscriber save(Subscriber subscriber);

    List<Subscriber> findByActive(boolean active);
    
}
package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.dto.CreateSubscriberRequest;
import kr.me.seesaw.entity.Subscriber;
import kr.me.seesaw.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 구독자 생성
     */
    @PostMapping("/subscribe")
    public ResponseEntity<Subscriber> subscribe(@RequestBody @Valid CreateSubscriberRequest request) {
        Subscriber subscriber = notificationService.registerSubscriber(request.getUserId(), request.getEndpoint());
        return ResponseEntity.ok(subscriber);
    }

    /**
     * 구독자 제거
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody Map<String, String> request) {
        String endpoint = request.get("endpoint");
        
        if (endpoint == null) {
            return ResponseEntity.badRequest().build();
        }
        
        notificationService.unregisterSubscriber(endpoint);
        return ResponseEntity.ok().build();
    }
}
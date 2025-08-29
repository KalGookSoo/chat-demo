package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.entity.Message;
import kr.me.seesaw.entity.Subscriber;
import kr.me.seesaw.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final SubscriberRepository subscriberRepository;

    private final EntityManager entityManager;

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    /**
     * Send push notifications to all active subscribers about a new message
     *
     * @param message The new message that was received
     */
    public void sendPushNotifications(Message message) {
        List<Subscriber> activeSubscribers = subscriberRepository.findByActive(true);

        if (activeSubscribers.isEmpty()) {
            logger.info("No active subscribers found for push notifications");
            return;
        }

        logger.info("Sending push notifications to {} subscribers", activeSubscribers.size());

        // Create notification payload
        String notificationPayload = createNotificationPayload(message);

        // Send notifications to all subscribers
        activeSubscribers.forEach(subscriber -> sendNotificationAsync(subscriber.getEndpoint(), notificationPayload));
    }

    /**
     * Asynchronously send a notification to a subscriber
     */
    @Async("notificationTaskExecutor")
    public void sendNotificationAsync(String endpoint, String payload) {
        try {
            sendNotification(endpoint, payload);
        } catch (Exception e) {
            logger.error("Failed to send notification to endpoint {}: {}",
                    endpoint, e.getMessage());
        }
    }

    /**
     * Create a JSON payload for the notification
     */
    private String createNotificationPayload(Message message) {
        return String.format("""
                        {
                            "title": "New Chat Message",
                            "body": "%s",
                            "senderId": "%s",
                            "createdDate": "%s"
                        }
                        """,
                escapeJson(message.getContent()),
                message.getSenderId(),
                message.getCreatedDate());
    }

    /**
     * Send the actual HTTP request to the subscriber's endpoint
     */
    private void sendNotification(String endpoint, String payload) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            logger.info("Successfully sent notification to endpoint: {}", endpoint);
        } else {
            logger.warn("Failed to send notification to endpoint: {}. Status code: {}",
                    endpoint, response.statusCode());
        }
    }

    /**
     * Simple method to escape JSON special characters in the message content
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 구독자 생성
     */
    public Subscriber registerSubscriber(String userId, String endpoint) {
        Subscriber subscriber = new Subscriber(userId, endpoint);
        return subscriberRepository.save(subscriber);
    }

    /**
     * 구독자 비활성화
     */
    public void unregisterSubscriber(String endpoint) {
        Subscriber subscriber = entityManager.getReference(Subscriber.class, endpoint);
        if (subscriber != null) {
            subscriber.setActive(false);
            subscriberRepository.save(subscriber);
        }
    }
}
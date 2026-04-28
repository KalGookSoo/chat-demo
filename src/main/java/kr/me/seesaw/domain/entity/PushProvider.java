package kr.me.seesaw.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PushProvider {
    WEB_PUSH("Web Push (VAPID)"),
    EXPO("Expo Push Notification");

    private final String description;
}

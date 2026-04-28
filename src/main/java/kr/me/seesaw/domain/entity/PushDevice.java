package kr.me.seesaw.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_push_device", uniqueConstraints = {
        @UniqueConstraint(name = "uk_push_device_token", columnNames = {"push_token"}),
        @UniqueConstraint(name = "uk_push_device_endpoint", columnNames = {"endpoint"})
})
@Comment("통합 푸시 기기 정보")
@DynamicInsert
@DynamicUpdate
public class PushDevice extends BaseEntity {

    @Comment("계정 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "user_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("푸시 서비스 제공자 (WEB_PUSH, EXPO)")
    private PushProvider provider;

    @Column(name = "push_token")
    @Comment("푸시 토큰 (EXPO 등)")
    private String pushToken;

    @Column(unique = true)
    @Comment("브라우저 푸시 서비스 주소 (WEB_PUSH)")
    private String endpoint;

    @Comment("공개키 (p256dh, WEB_PUSH)")
    private String p256dh;

    @Comment("인증 토큰 (auth, WEB_PUSH)")
    private String auth;

    @Comment("사용자 에이전트 또는 플랫폼 정보")
    private String userAgent;

    @Comment("기기 명칭")
    private String deviceName;

    @Column(nullable = false)
    @Builder.Default
    @Comment("활성 여부")
    private boolean active = true;

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateWebPush(String p256dh, String auth, String userAgent, String deviceName) {
        this.p256dh = p256dh;
        this.auth = auth;
        this.userAgent = userAgent;
        this.deviceName = deviceName;
        this.active = true;
    }

    public void updateExpoPush(String pushToken, String userAgent, String deviceName) {
        this.pushToken = pushToken;
        this.userAgent = userAgent;
        this.deviceName = deviceName;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

}

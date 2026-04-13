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
@Table(name = "tb_push_subscription", uniqueConstraints = @UniqueConstraint(columnNames = {"endpoint"}))
@Comment("웹 푸시 구독 정보")
@DynamicInsert
@DynamicUpdate
public class PushSubscription extends BaseEntity {

    @Comment("계정 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "user_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    @Comment("브라우저 푸시 서비스 주소")
    private String endpoint;

    @Comment("공개키 (p256dh)")
    private String p256dh;

    @Comment("인증 토큰 (auth)")
    private String auth;

    @Comment("사용자 에이전트 정보")
    private String userAgent;

    @Comment("기기 명칭")
    private String deviceName;

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateSubscription(String p256dh, String auth, String userAgent, String deviceName) {
        this.p256dh = p256dh;
        this.auth = auth;
        this.userAgent = userAgent;
        this.deviceName = deviceName;
    }

}

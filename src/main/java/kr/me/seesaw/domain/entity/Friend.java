package kr.me.seesaw.domain.entity;

import jakarta.persistence.*;
import kr.me.seesaw.domain.vo.FriendStatus;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Builder
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tb_friend", uniqueConstraints = {
        @UniqueConstraint(name = "uk_friend_user_friend", columnNames = {"user_id", "friend_id"})
})
@Comment("친구 관계")
@DynamicInsert
@DynamicUpdate
public class Friend extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @Comment("나")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", insertable = false, updatable = false)
    @Comment("친구")
    private User friend;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "friend_id", length = 36, nullable = false)
    private String friendId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("상태")
    private FriendStatus status;

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
    }

    public void block() {
        this.status = FriendStatus.BLOCKED;
    }

    public boolean isPending() {
        return FriendStatus.PENDING.equals(status);
    }

    public boolean isAccepted() {
        return FriendStatus.ACCEPTED.equals(status);
    }

    public boolean isBlocked() {
        return FriendStatus.BLOCKED.equals(status);
    }

}

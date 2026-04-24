package kr.me.seesaw.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "tb_view", uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}))
@Comment("조회")
@DynamicInsert
@DynamicUpdate
public class View {

    @Id
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    @Comment("식별자")
    private String id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일시")
    private LocalDateTime createdDate;

    @Comment("메시지 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private Message message;

    @Column(name = "message_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String messageId;

    @Comment("계정 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "user_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String userId;

}

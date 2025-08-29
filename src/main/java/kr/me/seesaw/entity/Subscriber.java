package kr.me.seesaw.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_subscriber")
@Comment("구독자")
@DynamicInsert
@DynamicUpdate
public class Subscriber extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String endpoint;

    @Setter
    @Column(nullable = false)
    private boolean active = true;

    public Subscriber(String userId, String endpoint) {
        this.userId = userId;
        this.endpoint = endpoint;
    }

}
package kr.me.seesaw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode

@Table(name = "tb_message")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String senderId;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    public Message(String content, String senderId) {
        this.content = content;
        this.senderId = senderId;
    }

}
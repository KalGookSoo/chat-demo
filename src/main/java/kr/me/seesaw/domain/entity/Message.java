package kr.me.seesaw.domain.entity;

import jakarta.persistence.*;
import kr.me.seesaw.domain.vo.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "tb_message")
@Comment("메시지")
@DynamicInsert
@DynamicUpdate
public class Message extends BaseEntity {

    @Comment("발신자 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @Column(name = "sender_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String senderId;

    @Comment("채팅방 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @Column(name = "chat_room_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String chatRoomId;

    @Column(nullable = false)
    @Comment("메시지 본문")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("메시지 타입")
    private MessageType type;

    @Column
    @Comment("미디어 타입")
    private String mimeType;

}
package kr.me.seesaw.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tb_message")
@Comment("메시지")
@DynamicInsert
@DynamicUpdate
public class Message extends BaseEntity {

    @Column(length = 36, nullable = false)
    @Comment("발신자 식별자")
    private String senderId;

    @Column(length = 36, nullable = false)
    @Comment("채팅방 식별자")
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

    public Message(String content, String senderId, String chatRoomId, MessageType type, String mimeType) {
        this.content = content;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.type = type;
        this.mimeType = mimeType;
    }

}
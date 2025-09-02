package kr.me.seesaw.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"chatRoom"})
@ToString(callSuper = true, exclude = {"chatRoom"})
@Entity
@Table(name = "tb_chat_room_member")
@Comment("채팅방 계정 매핑")
@DynamicInsert
@DynamicUpdate
public class ChatRoomMember extends BaseEntity {
    @Comment("채팅방 식별자")
    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @Column(length = 36, nullable = false)
    @Comment("계정 식별자")
    private String userId;

    protected void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}

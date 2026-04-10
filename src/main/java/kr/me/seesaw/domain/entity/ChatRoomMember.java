package kr.me.seesaw.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "tb_chat_room_member", uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
@Comment("채팅방 계정 매핑")
@DynamicInsert
@DynamicUpdate
public class ChatRoomMember extends BaseEntity {

    @Comment("채팅방 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @Comment("계정 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "user_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String userId;

    @Column(name = "chat_room_id", length = 36, insertable = false, updatable = false, nullable = false)
    private String chatRoomId;

}

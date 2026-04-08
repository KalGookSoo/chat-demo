package kr.me.seesaw.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"chatRoom"})
@ToString(callSuper = true, exclude = {"chatRoom"})
@Entity
@Table(name = "tb_chat_room_member", uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
@Comment("채팅방 계정 매핑")
@DynamicInsert
@DynamicUpdate
public class ChatRoomMember extends BaseEntity {

    @Comment("채팅방 식별자")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @Comment("계정 식별자")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(length = 36, insertable = false, updatable = false, nullable = false)
    @Comment("계정 식별자")
    private String userId;

    @Column(name = "chat_room_id", insertable = false, updatable = false, nullable = false)
    private String chatRoomId;

}

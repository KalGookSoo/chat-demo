package kr.me.seesaw.entity;

import jakarta.persistence.*;
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
@Table(
        name = "tb_chat_room_member",
        uniqueConstraints = @UniqueConstraint(name = "uk_chat_room_member_room_user", columnNames = {"chatRoomId", "userId"}),
        indexes = {
                @Index(name = "idx_chat_room_member_room", columnList = "chatRoomId"),
                @Index(name = "idx_chat_room_member_user", columnList = "userId")
        }
)
@Comment("채팅방 계정 매핑")
@DynamicInsert
@DynamicUpdate
public class ChatRoomMember extends BaseEntity {

    @Column(length = 36, nullable = false)
    @Comment("채팅방 식별자")
    private String chatRoomId;

    @Column(length = 36, nullable = false)
    @Comment("계정 식별자")
    private String userId;

}

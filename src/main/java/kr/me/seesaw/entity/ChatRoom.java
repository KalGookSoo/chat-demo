package kr.me.seesaw.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tb_chat_room")
@Comment("채팅방")
@DynamicInsert
@DynamicUpdate
public class ChatRoom extends BaseEntity {

    @Comment("이름")
    private String name;


}

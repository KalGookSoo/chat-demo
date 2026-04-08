package kr.me.seesaw.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Setter
@Getter
@Entity
@Table(name = "tb_chat_room")
@Comment("채팅방")
@DynamicInsert
@DynamicUpdate
public class ChatRoom extends BaseEntity {

    @Comment("이름")
    private String name;

}

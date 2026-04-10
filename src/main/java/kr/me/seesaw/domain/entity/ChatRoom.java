package kr.me.seesaw.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "tb_chat_room")
@Comment("채팅방")
@DynamicInsert
@DynamicUpdate
public class ChatRoom extends BaseEntity {

    @Comment("이름")
    private String name;

}

package kr.me.seesaw.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true, exclude = "members")
@ToString(callSuper = true, exclude = "members")
@Entity
@Table(name = "tb_chat_room")
@Comment("채팅방")
@DynamicInsert
@DynamicUpdate
public class ChatRoom extends BaseEntity {
    @Comment("이름")
    private String name;

    @OneToMany(mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ChatRoomMember> members = new HashSet<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public void addMember(ChatRoomMember member) {
        members.add(member);
        if (member.getChatRoom() == null) {
            member.setChatRoom(this);
        }
    }
}

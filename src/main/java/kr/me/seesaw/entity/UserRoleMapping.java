package kr.me.seesaw.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "tb_user_role_mapping")
@Comment("계정 역할 매핑")
@DynamicInsert
@DynamicUpdate
public class UserRoleMapping extends BaseEntity {

    @Column(length = 36, nullable = false)
    @Comment("계정 식별자")
    private String userId;

    @Column(length = 36, nullable = false)
    @Comment("역할 식별자")
    private String roleId;

}

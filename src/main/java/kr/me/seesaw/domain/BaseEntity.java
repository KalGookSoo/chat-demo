package kr.me.seesaw.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
abstract public class BaseEntity implements Serializable {

    @Id
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    @Comment("식별자")
    private String id;

    @CreatedBy
    @Comment("생성자")
    private String createdBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일시")
    private LocalDateTime createdDate;

    @Version
    @Comment("버전")
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

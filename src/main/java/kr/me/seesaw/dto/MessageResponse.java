package kr.me.seesaw.dto;

import kr.me.seesaw.domain.MessageType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class MessageResponse implements Serializable {
    private String id;

    private String senderId;

    private String chatRoomId;

    private String content;

    private MessageType type;

    private String mimeType;

    private LocalDateTime createdDate;
}

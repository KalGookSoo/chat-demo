package kr.me.seesaw.domain.dto;

import kr.me.seesaw.domain.vo.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class MessageResponse {

    private String id;

    private String chatRoomId;

    private String content;

    private MessageType type;

    private String mimeType;

    private LocalDateTime createdDate;

    private SenderResponse sender;

}

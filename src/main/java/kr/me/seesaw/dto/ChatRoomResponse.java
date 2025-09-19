package kr.me.seesaw.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ChatRoomResponse implements Serializable {

    private String id;

    private String name;

}

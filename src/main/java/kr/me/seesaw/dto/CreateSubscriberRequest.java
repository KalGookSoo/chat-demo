package kr.me.seesaw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public final class CreateSubscriberRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String endpoint;

}

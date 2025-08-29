package kr.me.seesaw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public final class DeleteSubscriberRequest {

    @NotBlank
    private String endpoint;

}

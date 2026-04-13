package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.validation.FieldError;

@Builder
@Schema(description = "유효성 검사 오류 정보")
public record ValidationError(
        @Schema(description = "오류 코드")
        String code,
        @Schema(description = "오류 메시지")
        String message,
        @Schema(description = "오류 발생 필드")
        String field,
        @Schema(description = "거절된 값")
        Object rejectedValue
) {

    public ValidationError(FieldError error) {
        this(error.getCode(), error.getDefaultMessage(), error.getField(), error.getRejectedValue());
    }

}
package kr.me.seesaw.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.FieldError;

@EqualsAndHashCode
@ToString
@Getter
public final class ValidationError {

    private final String code;

    private final String message;

    private final String field;

    private final Object rejectedValue;

    public ValidationError(FieldError error) {
        this.code = error.getCode();
        this.message = error.getDefaultMessage();
        this.field = error.getField();
        this.rejectedValue = error.getRejectedValue();
    }

}
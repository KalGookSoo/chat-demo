package kr.me.seesaw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.FieldError;

import java.io.Serializable;

@EqualsAndHashCode
@ToString
@Getter
public final class ValidationError implements Serializable {

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
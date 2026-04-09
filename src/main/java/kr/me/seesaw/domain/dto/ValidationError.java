package kr.me.seesaw.domain.dto;

import org.springframework.validation.FieldError;

public record ValidationError(String code, String message, String field, Object rejectedValue) {

    public ValidationError(FieldError error) {
        this(error.getCode(), error.getDefaultMessage(), error.getField(), error.getRejectedValue());
    }

}
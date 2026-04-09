package kr.me.seesaw.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(@NotBlank String username, @NotBlank String password, String name) {
}

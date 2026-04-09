package kr.me.seesaw.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInRequest(@NotBlank String username, @NotBlank String password) {
}

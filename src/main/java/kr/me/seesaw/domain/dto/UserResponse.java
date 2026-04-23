package kr.me.seesaw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "사용자 정보 응답")
public record UserResponse(
        @Schema(description = "식별자")
        String id,
        @Schema(description = "계정명")
        String username,
        @Schema(description = "이름")
        String name,
        @Schema(description = "가입일")
        LocalDateTime registeredAt,
        @Schema(description = "연락처")
        String contactNumber,
        @Schema(description = "만료 일시")
        LocalDateTime expiredDate,
        @Schema(description = "잠금 일시")
        LocalDateTime lockedDate,
        @Schema(description = "패스워드 만료 일시")
        LocalDateTime credentialsExpiredDate
) {

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .registeredAt(user.getCreatedDate())
                .contactNumber(user.getContactNumber())
                .expiredDate(user.getExpiredDate())
                .lockedDate(user.getLockedDate())
                .credentialsExpiredDate(user.getCredentialsExpiredDate())
                .build();
    }

}

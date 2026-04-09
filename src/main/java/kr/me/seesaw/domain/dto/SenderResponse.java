package kr.me.seesaw.domain.dto;

import lombok.Builder;

@Builder
public record SenderResponse(String id, String name) {
}

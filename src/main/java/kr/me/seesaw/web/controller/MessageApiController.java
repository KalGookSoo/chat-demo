package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.me.seesaw.domain.dto.MessageResponse;
import kr.me.seesaw.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "메시지 API", description = "메시지 이력 조회를 담당합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageApiController {

    private final MessageService messageService;

    @Operation(summary = "메시지 목록 조회", description = "특정 채팅방의 메시지 이력을 페이징하여 조회합니다.")
    @PreAuthorize("isAuthenticated() and @chatRoomContext.isMember(#chatRoomId, authentication.details)")
    @GetMapping
    public ResponseEntity<PagedModel<MessageResponse>> getMessages(
            @Parameter(description = "채팅방 식별자") @RequestParam String chatRoomId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "30") int pageSize
    ) {
        Page<MessageResponse> page = messageService.getMessagesByChatRoomId(chatRoomId,
                pageNumber, pageSize);
        PagedModel<MessageResponse> pagedModel = new PagedModel<>(page);
        return ResponseEntity.ok(pagedModel);
    }

}

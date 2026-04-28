package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.me.seesaw.component.security.PrincipalProvider;
import kr.me.seesaw.domain.dto.PushDeviceRegisterRequest;
import kr.me.seesaw.domain.dto.PushDeviceResponse;
import kr.me.seesaw.service.PushDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Push Device API", description = "통합 푸시 기기 관리 API")
@RestController
@RequestMapping("/api/push/devices")
@RequiredArgsConstructor
public class PushDeviceApiController {

    private final PushDeviceService pushDeviceService;

    private final PrincipalProvider principalProvider;

    @Operation(summary = "푸시 기기 등록/갱신", description = "PWA 또는 Expo 앱의 푸시 정보를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PushDeviceResponse register(@RequestBody @Valid PushDeviceRegisterRequest request) {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        return pushDeviceService.registerDevice(userId, request);
    }

    @Operation(summary = "내 푸시 기기 목록 조회")
    @GetMapping("/me")
    public List<PushDeviceResponse> getMyDevices() {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        return pushDeviceService.getMyDevices(userId);
    }

    @Operation(summary = "푸시 기기 등록 해제")
    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@PathVariable String deviceId) {
        Authentication authentication = principalProvider.getAuthentication();
        String userId = authentication.getDetails().toString();
        pushDeviceService.unregisterDevice(userId, deviceId);
    }

}

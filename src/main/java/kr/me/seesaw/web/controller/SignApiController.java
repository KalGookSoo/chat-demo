package kr.me.seesaw.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.me.seesaw.domain.dto.*;
import kr.me.seesaw.service.AuthenticationService;
import kr.me.seesaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "회원가입, 로그인 및 토큰 관리를 담당합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignApiController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        userService.createUser(request.username(), request.password(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "사용자 인증을 수행하고 JWT 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @PostMapping("/sign-in")
    public ResponseEntity<JsonWebToken> signIn(@Valid @RequestBody SignInRequest request) {
        JsonWebToken token = authenticationService.authenticate(request);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공")
    @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    @PostMapping("/token/refresh")
    public ResponseEntity<JsonWebToken> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JsonWebToken token = authenticationService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "로그아웃", description = "액세스 토큰과 리프레시 토큰을 파기합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(@Valid @RequestBody TokenRevokeRequest request) {
        authenticationService.revokeTokens(request.accessToken(), request.refreshToken());
        return ResponseEntity.ok().build();
    }

}

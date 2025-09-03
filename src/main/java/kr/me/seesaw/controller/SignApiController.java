package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.dto.JsonWebToken;
import kr.me.seesaw.dto.SignInRequest;
import kr.me.seesaw.dto.SignUpRequest;
import kr.me.seesaw.dto.TokenRefreshRequest;
import kr.me.seesaw.service.AuthenticationService;
import kr.me.seesaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignApiController {
    private final UserService userService;

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        userService.createUser(request.getUsername(), request.getPassword(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JsonWebToken> signIn(@Valid @RequestBody SignInRequest request) {
        JsonWebToken response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<JsonWebToken> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JsonWebToken response = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

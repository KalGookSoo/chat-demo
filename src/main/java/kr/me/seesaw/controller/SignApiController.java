package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.dto.JsonWebToken;
import kr.me.seesaw.dto.SignInRequest;
import kr.me.seesaw.dto.TokenRefreshRequest;
import kr.me.seesaw.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignApiController {

    private final AuthenticationService authenticationService;

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

package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.JsonWebToken;
import kr.me.seesaw.domain.dto.SignInRequest;

public interface AuthenticationService {

    JsonWebToken authenticate(SignInRequest request);

    JsonWebToken refreshToken(String refreshToken);

}

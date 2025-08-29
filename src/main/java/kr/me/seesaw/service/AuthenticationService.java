package kr.me.seesaw.service;

import kr.me.seesaw.dto.JsonWebToken;
import kr.me.seesaw.dto.SignInRequest;

public interface AuthenticationService {

    JsonWebToken authenticate(SignInRequest request);

    JsonWebToken refreshToken(String refreshToken);

}

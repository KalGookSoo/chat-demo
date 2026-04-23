package kr.me.seesaw.component.security;

import java.time.Instant;

public interface TokenRevocationService {

    void revoke(String token, Instant expiresAt);

    boolean isRevoked(String token);

}

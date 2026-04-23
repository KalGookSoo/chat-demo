package kr.me.seesaw.component.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryTokenRevocationService implements TokenRevocationService {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    @Override
    public void revoke(String token, Instant expiresAt) {
        if (token == null || token.isBlank()) {
            return;
        }

        if (expiresAt == null || !expiresAt.isAfter(Instant.now())) {

            revokedTokens.remove(token);
            return;
        }

        revokedTokens.put(token, expiresAt);
    }

    @Override
    public boolean isRevoked(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        cleanupExpiredEntries();
        Instant expiresAt = revokedTokens.get(token);
        return expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    private void cleanupExpiredEntries() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
    }

}

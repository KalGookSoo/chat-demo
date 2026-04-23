package kr.me.seesaw.component.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.me.seesaw.domain.dto.JsonWebToken;
import kr.me.seesaw.domain.dto.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 액세스 토큰 만료 시간: 1시간
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;

    // 리프레시 토큰 만료 시간: 14일
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14;

    private final String secretKey;

    private final TokenRevocationService tokenRevocationService;

    public JsonWebToken generateTokenInfo(String userId, String username, Collection<String> authorities) {
        log.debug("액세스 토큰과 리프레시 토큰을 생성합니다. userId: {}, username: {}, authorities: {}", userId, username, authorities);
        String accessToken = generateAccessToken(userId, username, authorities);
        String refreshToken = generateRefreshToken(userId, username, authorities);
        return JsonWebToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(ACCESS_TOKEN_EXPIRATION)
                .build();
    }

    /**
     * 계정 인증 주체 정보를 암호화한 액세스 토큰을 반환합니다.
     */
    private String generateAccessToken(String userId, String username, Collection<String> authorities) {
        log.debug("액세스 토큰을 생성합니다. userId: {}, username: {}, authorities: {}", userId, username, authorities);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);
        SecretKey secretKey = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     */
    private String generateRefreshToken(String userId, String username, Collection<String> authorities) {
        log.debug("리프레시 토큰을 생성합니다. userId: {}, username: {}, authorities: {}", userId, username, authorities);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);
        SecretKey secretKey = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("authorities", authorities)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 토큰 정보 객체
     */
    public JsonWebToken refreshToken(String refreshToken) {
        log.debug("리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다. refreshToken: {}", refreshToken);
        if (tokenRevocationService.isRevoked(refreshToken)) {
            throw new BadCredentialsException("폐기된 리프레시 토큰입니다.");
        }

        log.debug("리프레시 토큰 유효성 검증을 시작합니다.");
        log.debug("리프레시 토큰을 파싱합니다.");
        Claims claims = parseClaims(refreshToken);

        log.debug("리프레시 토큰에서 계정 식별자와 권한 정보를 추출합니다.");
        String userId = claims.getSubject();
        String username = claims.get("username", String.class);
        @SuppressWarnings("noinspection unchecked")
        Collection<String> authorities = claims.get("authorities", Collection.class);
        if (authorities == null || authorities.isEmpty()) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }

        String newAccessToken = generateAccessToken(userId, username, authorities);
        String newRefreshToken = generateRefreshToken(userId, username, authorities);
        return JsonWebToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(ACCESS_TOKEN_EXPIRATION)
                .build();
    }

    /**
     * JWT 토큰을 검증하고 인증 객체를 반환합니다.
     *
     * @param token JWT 토큰
     * @return 인증 객체
     */
    public Authentication validateTokenAndGetAuthentication(String token) {
        log.debug("JWT 토큰 유효성 검증을 시작합니다.");
        try {
            if (tokenRevocationService.isRevoked(token)) {
                throw new BadCredentialsException("폐기된 JWT 토큰입니다.");
            }

            Claims claims = parseClaims(token);
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);
            Collection<?> authorities = claims.get("authorities", Collection.class);
            Collection<GrantedAuthority> grantedAuthorities = authorities.stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.toString()))
                    .collect(Collectors.toList());
            // 인증된 사용자 정보를 담은 Authentication 객체 생성
            JwtUserDetails principal = new JwtUserDetails(userId, username, grantedAuthorities);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
            usernamePasswordAuthenticationToken.setDetails(userId);
            return usernamePasswordAuthenticationToken;
        } catch (SignatureException e) {
            throw new BadCredentialsException("유효하지 않은 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException("유효하지 않은 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new BadCredentialsException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("JWT 토큰이 비어있습니다.");
        }
    }

    public void revoke(String token) {
        log.info("토큰을 파기합니다. token: {}", token);
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        Instant expiresAt = expiration == null ? null : expiration.toInstant();
        tokenRevocationService.revoke(token, expiresAt);
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

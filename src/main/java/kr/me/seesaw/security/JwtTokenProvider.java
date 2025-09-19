package kr.me.seesaw.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.me.seesaw.dto.JsonWebToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtTokenProvider {

    private final String secretKey;

    // 액세스 토큰 만료 시간: 1시간
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;

    // 리프레시 토큰 만료 시간: 14일
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14;

    public JsonWebToken generateTokenInfo(String userId, String username, Collection<String> authorities) {
        String accessToken = generateAccessToken(userId, username, authorities);
        String refreshToken = generateRefreshToken(userId, username, authorities);
        return new JsonWebToken(accessToken, refreshToken, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 계정 인증 주체 정보를 암호화한 액세스 토큰을 반환합니다.
     */
    private String generateAccessToken(String userId, String username, Collection<String> authorities) {
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
        // 리프레시 토큰 유효성 검증
        SecretKey key = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
        // 리프레시 토큰 파싱
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        // 계정 식별자 추출
        String userId = claims.getSubject();
        String username = claims.get("username", String.class);
        @SuppressWarnings("noinspection unchecked")
        Collection<String> authorities = claims.get("authorities", Collection.class);
        if (authorities == null || authorities.isEmpty()) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }
        // 새로운 액세스 토큰 생성
        String newAccessToken = generateAccessToken(userId, username, authorities);
        // 새로운 리프레시 토큰 생성 (선택적)
        String newRefreshToken = generateRefreshToken(userId, username, authorities);
        return new JsonWebToken(newAccessToken, newRefreshToken, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * JWT 토큰을 검증하고 인증 객체를 반환합니다.
     *
     * @param token JWT 토큰
     * @return 인증 객체
     */
    public Authentication validateTokenAndGetAuthentication(String token) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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

}

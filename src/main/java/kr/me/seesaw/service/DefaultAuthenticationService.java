package kr.me.seesaw.service;

import kr.me.seesaw.component.security.JwtTokenProvider;
import kr.me.seesaw.domain.dto.JsonWebToken;
import kr.me.seesaw.domain.dto.SignInRequest;
import kr.me.seesaw.domain.entity.Role;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public JsonWebToken authenticate(SignInRequest request) {
        log.debug("사용자 인증을 시작합니다. username: {}", request.username());
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("사용자명 또는 패스워드가 일치하지 않습니다"));

        log.debug("패스워드 일치 여부를 확인합니다.");
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("사용자명 또는 패스워드가 일치하지 않습니다");
        }

        log.debug("계정 상태를 확인합니다.");
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("만료된 계정입니다");
        }

        log.debug("계정 잠금 상태를 확인합니다.");
        if (!user.isAccountNonLocked()) {
            throw new LockedException("잠긴 계정입니다");
        }

        log.debug("패스워드 만료 상태를 확인합니다.");
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("패스워드가 만료되었습니다");
        }

        log.debug("액세스 토큰과 리프레시 토큰을 생성합니다.");
        List<String> authorities = user.getRoles().stream().map(Role::getName).toList();
        return jwtTokenProvider.generateTokenInfo(user.getId(), user.getUsername(), authorities);
    }

    @Override
    public JsonWebToken refreshToken(String refreshToken) {
        log.debug("리프레시 토큰을 재발급합니다.");
        return jwtTokenProvider.refreshToken(refreshToken);
    }

}

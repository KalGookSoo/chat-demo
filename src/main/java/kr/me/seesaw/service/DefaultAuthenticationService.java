package kr.me.seesaw.service;

import kr.me.seesaw.dto.JsonWebToken;
import kr.me.seesaw.dto.SignInRequest;
import kr.me.seesaw.dto.UserPrincipal;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.domain.UserRoleMapping;
import kr.me.seesaw.repository.RoleRepository;
import kr.me.seesaw.repository.UserRepository;
import kr.me.seesaw.repository.UserRoleMappingRepository;
import kr.me.seesaw.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

    private final UserRepository userRepository;

    private final UserRoleMappingRepository userRoleMappingRepository;

    private final RoleRepository roleRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Override
    public JsonWebToken authenticate(SignInRequest request) {
        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("사용자명 또는 패스워드가 일치하지 않습니다"));

        // 사용자가 가진 역할 조회
        List<UserRoleMapping> userRoleMappings = userRoleMappingRepository.findAllByUserId(user.getId());
        List<String> roleIds = userRoleMappings.stream()
                .map(UserRoleMapping::getRoleId)
                .toList();

        // 사용자가 가진 역할 할당
        new LinkedHashSet<>(roleRepository.findAllByIdIn(roleIds)).forEach(user::addRole);

        UserPrincipal userPrincipal = new UserPrincipal(user);

        // 패스워드 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("사용자명 또는 패스워드가 일치하지 않습니다");
        }

        // 계정 상태 확인
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("만료된 계정입니다");
        }

        if (!user.isAccountNonLocked()) {
            throw new LockedException("잠긴 계정입니다");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("패스워드가 만료되었습니다");
        }

        // 액세스 토큰과 리프레시 토큰 생성
        return jwtTokenProvider.generateTokenInfo(userPrincipal);
    }

    @Override
    public JsonWebToken refreshToken(String refreshToken) {
        return jwtTokenProvider.refreshToken(refreshToken);
    }

}

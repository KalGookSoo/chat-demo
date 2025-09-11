package kr.me.seesaw.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Bearer 토큰이 존재하는 경우 처리
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 추출
            try {
                Authentication authentication = jwtTokenProvider.validateTokenAndGetAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                if (!response.isCommitted()) {
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    String body = objectMapper.writeValueAsString(Map.of("message", e.getMessage()));
                    response.getWriter().write(body);
                }
                return; // 필터체인 중단
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // Controller 메서드의 보안 애노테이션(@PreAuthorize, @Secured) 존재 여부로 필터 적용 여부를 결정
        try {
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerExecutionChain == null) {
                // 핸들러가 없으면(정적 리소스 등) 필터를 적용하지 않음
                return true;
            }
            Object handler = handlerExecutionChain.getHandler();
            if (handler instanceof HandlerMethod handlerMethod) {
                Method method = handlerMethod.getMethod();
                boolean hasPreAuthorize = method.isAnnotationPresent(PreAuthorize.class);
                boolean hasSecured = method.isAnnotationPresent(Secured.class);
                // 보안 애노테이션이 존재하면 필터를 적용(=false), 없으면 스킵(=true)
                return !(hasPreAuthorize || hasSecured);
            }
        } catch (Exception e) {
            // 핸들러 확인 중 예외가 발생한 경우 보수적으로 필터를 적용(=false)하여
            // Security 체인 내에서 AuthenticationException이 발생하도록 유도
            return false;
        }
        // HandlerMethod가 아닌 경우(예: 리소스 핸들러 등)는 필터를 스킵
        return true;

    }
}

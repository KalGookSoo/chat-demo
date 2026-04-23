# 코딩 컨벤션 (Coding Convention)

chat-demo 프로젝트는 일관된 유지보수성과 예측 가능한 변경을 위해 아래 코딩 컨벤션을 따릅니다.

## 1. DTO 설계 규칙

- DTO는 `record`를 기본으로 사용합니다.
- DTO 생성은 `@Builder` 패턴을 사용합니다.
- 엔티티를 DTO로 변환할 때는 정적 팩터리(`from`)를 권장합니다.
- 예시: `UserResponse`는 `record + @Builder`를 사용합니다.

## 2. 엔티티 연관관계 규칙

- 엔티티 설계 시 `OneToMany` 직접 매핑을 지양합니다.
- 목록 조회는 연관 컬렉션 탐색 대신 `IN` 조건 기반 조회 후 애플리케이션 레이어에서 메모리 조인으로 조합합니다.
- `OneToMany`에 대응되는 집계/목록 모델은 엔티티가 아닌 DTO에서 처리합니다.
- 목적:
    - 순환참조 위험 감소 (양방향 연관으로 인한 구조 결합 방지)
    - 불필요한 컬렉션 로딩 방지
    - N+1 및 영속성 컨텍스트 복잡도 최소화
    - 조회/응답 모델 분리

## 3. Repository 규칙

- Repository 인터페이스는 `JpaRepository`가 아닌 `org.springframework.data.repository.Repository`를 확장합니다.
- 필요한 메서드만 명시적으로 선언해 저장소 책임을 제한합니다.

## 4. 엔티티 수정 규칙

- 엔티티 수정(상태 변경)이 목적일 때는 `findById`보다 `getReferenceById` 사용을 우선하여 불필요한 SELECT를 방지합니다.
- 트랜잭션 내 더티 체킹(Dirty Checking) 기반 갱신을 기본 전략으로 사용합니다.

## 5. 예외 처리 규칙

- 예외 응답 변환은 `@ExceptionHandler` 또는 전역 `ControllerAdvice`에서 처리합니다.
- `Controller`와 `Service`는 예외 포매팅/응답 바디 구성에 직접 관여하지 않습니다.
- 서비스는 도메인 관점의 예외를 발생시키고, 응답 정책은 예외 처리 계층에서 일괄 관리합니다.

## 6. 서비스/컨텍스트 컴포넌트 규칙

- **비즈니스 로직:** 비즈니스 프로세스와 상태 변경 로직은 `Service` 컴포넌트에서 담당합니다.
- **HTTP 의존 분리:** `Service` 레이어는 HTTP/서블릿 및 웹 보안 구현에 직접 의존하지 않습니다.
    - 금지 대상: `HttpSession`, `SecurityContext`, `HttpServletRequest`, `HttpServletResponse` 등 웹 요청/응답 객체 직접 참조
    - 처리 원칙: 필요한 사용자/권한/요청 정보는 컨트롤러 또는 `Context` 컴포넌트에서 해석 후 서비스 메서드 파라미터로 전달
- **교차 관심사 분리 (Context):** 권한 확인, 현재 사용자 조회 등 반복되는 조회 및 정보 제공 책임은 별도의 `Context` 컴포넌트로 분리하여 서비스 레이어의 복잡도를 낮춥니다.
    - **의존성 규칙:** `Context`는 다른 `Service`를 참조하지 않고 오직 `Repository` 또는 보안 컨텍스트만 참조하여 순환 참조를 방지합니다.
- **@RequestScope를 통한 효율화:**
    - 요청 단위 데이터 일관성과 성능을 위해 `Context` 컴포넌트에 `@RequestScope`를 적용합니다. 주입 안정성을 위해 `proxyMode` 설정을 포함합니다.
    - **명시적 메모이제이션(Memoization):** 동일 요청 내 중복 쿼리를 방지하기 위해 내부 필드(Map, 변수 등)를 활용한 캐싱 로직을 반드시 구현합니다.
- **보안 선언부 관리:**
    - `@PreAuthorize`에서 `Context` 빈을 직접 호출하여 권한을 체크합니다. (예: `@PreAuthorize("@chatRoomContext.isMember(#roomId, principal.username)")`)
    - 이때 IDE의 정적 분석 기능을 활용할 수 있도록 빈 이름을 명확하게 관리하고, 메서드 시그니처를 간결하게 유지합니다.
- **목적:**
    - 비즈니스 로직과 권한/조회 로직의 명확한 계층 분리
    - 서비스 로직의 HTTP 기술 종속 제거로 테스트 용이성 및 변경 내성 강화
    - 불필요한 레이어(Evaluator 등) 추가 없이 IDE의 지원을 받으며 생산성 극대화
    - 요청 내 중복 조회 최소화로 DB 부하 감소

## 7. 기타 일반 규칙

- 명시되지 않은 코딩 스타일은 Java Language Specification과 Spring Framework 관례를 따릅니다.
- 네이밍, 패키지 구조, 애너테이션 사용은 기존 코드베이스의 일관성을 우선합니다.

package kr.me.seesaw.component.security;

import org.springframework.security.core.Authentication;

public interface PrincipalProvider {

    Authentication getAuthentication();

}

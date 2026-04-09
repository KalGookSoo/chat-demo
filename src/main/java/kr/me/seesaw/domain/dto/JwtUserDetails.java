package kr.me.seesaw.domain.dto;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
public record JwtUserDetails(String userId, String username, Collection<? extends GrantedAuthority> authorities) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities == null ? List.of() : authorities;
    }

    @Override
    public String getPassword() {
        return "[PROTECTED]";
    }

    @Override
    public String getUsername() {
        return username();
    }

}

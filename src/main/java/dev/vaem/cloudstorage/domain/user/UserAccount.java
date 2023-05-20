package dev.vaem.cloudstorage.domain.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAccount {

    private String id;
    
    private Set<String> roles;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}

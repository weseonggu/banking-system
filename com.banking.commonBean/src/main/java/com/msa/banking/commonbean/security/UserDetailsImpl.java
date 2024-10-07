package com.msa.banking.commonbean.security;

import com.msa.banking.common.auth.dto.ForContext;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private UUID userId;

    private String username;

    @Getter
    private String role;

    public UserDetailsImpl(ForContext context) {
        this.userId = context.getId();
        this.username = context.getUsername();
        this.role = context.getRole();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = role;

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

}

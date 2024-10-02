package com.msa.banking.commonbean.security;

import com.msa.banking.common.auth.response.AuthFeignResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final AuthFeignResponseDto authResponse;

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return authResponse.getUsername();
    }

    public UUID getUserId() {
        return authResponse.getId();
    }

    public String getRole() {
        return authResponse.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = authResponse.getRole();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

}

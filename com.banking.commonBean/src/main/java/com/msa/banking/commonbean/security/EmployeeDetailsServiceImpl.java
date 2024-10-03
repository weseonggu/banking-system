package com.msa.banking.commonbean.security;

import com.msa.banking.common.auth.response.AuthFeignResponseDto;
import com.msa.banking.commonbean.client.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeDetailsServiceImpl implements UserDetailsService {

    private final AuthClient authClient;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println("EmployeeDetailsServiceImpl 들어온다 ");

        AuthFeignResponseDto findEmployee = authClient.findEmployeeId(userId);

        System.out.println("EmployeeDetailsServiceImpl 조회 완료");
        return new UserDetailsImpl(findEmployee);
    }
}

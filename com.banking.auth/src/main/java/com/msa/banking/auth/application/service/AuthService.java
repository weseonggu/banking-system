package com.msa.banking.auth.application.service;

import com.msa.banking.auth.application.jwt.JwtUtil;
import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.auth.infrastructure.repository.CustomerRepository;
import com.msa.banking.auth.infrastructure.repository.EmployeeRepository;
import com.msa.banking.auth.presentation.request.AuthSignInRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignUpRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     * @param request
     * @return
     */
    @Transactional
    public AuthResponseDto createAuth(AuthSignUpRequestDto request) {

        if (UserRole.valueOf(request.getRole()) == UserRole.MASTER || UserRole.valueOf(request.getRole()) == UserRole.MANAGER) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            Employee employee = AuthSignUpRequestDto.toEmployee(request);
            Employee savedEmployee = null;

            try {
                savedEmployee = employeeRepository.save(employee);
            } catch (ConstraintViolationException e) {
                throw new GlobalCustomException(ErrorCode.DUPLICATE_RESOURCE);
            }

            return AuthResponseDto.toDto(savedEmployee);


        }else {
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            Customer customer = AuthSignUpRequestDto.toCustomer(request);
            Customer savedCustomer = null;

            try {
                savedCustomer = customerRepository.save(customer);
            } catch (ConstraintViolationException e) {
                throw new GlobalCustomException(ErrorCode.DUPLICATE_RESOURCE);
            }

            return AuthResponseDto.toDto(savedCustomer);
        }
    }

    /**
     * 로그인
     * @param request
     * @return
     */
    public String signInAuth(AuthSignInRequestDto request) {

        if (request.getRole().equals(UserRole.MASTER.name()) || request.getRole().equals(UserRole.MANAGER.name())) {

            Employee findEmployee = employeeRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            if (!passwordEncoder.matches(request.getPassword(), findEmployee.getPassword())) {
                throw new GlobalCustomException(ErrorCode.PASSWORD_BAD_REQUEST);
            }

            return jwtUtil.createToken(findEmployee.getId().toString(), findEmployee.getUsername(), findEmployee.getRole().name());
        } else {

            Customer findCustomer = customerRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            if (!passwordEncoder.matches(request.getPassword(), findCustomer.getPassword())) {
                throw new GlobalCustomException(ErrorCode.PASSWORD_BAD_REQUEST);
            }

            return jwtUtil.createToken(findCustomer.getId().toString(), findCustomer.getUsername(), findCustomer.getRole().name());
        }

    }
}

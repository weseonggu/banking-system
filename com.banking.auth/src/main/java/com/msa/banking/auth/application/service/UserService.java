package com.msa.banking.auth.application.service;

import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.auth.infrastructure.repository.CustomerRepository;
import com.msa.banking.auth.infrastructure.repository.EmployeeRepository;
import com.msa.banking.auth.presentation.request.AuthRequestDto;
import com.msa.banking.auth.presentation.request.SearchRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 내부 API 직원 테이블 전용
     * username 유저 단 건 조회
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "EmployeeCache", key = "#userId", condition = "@checkRedisState.isRedisAvailable()")
    public AuthResponseDto findEmployeeUsername(UUID userId) {
        Employee findEmployee = employeeRepository.findById(userId).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.toDto(findEmployee);
    }

    /**
     * 내부 API 고객 테이블 전용
     * username 유저 단 건 조회
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "CustomerCache", key = "#userId", condition = "@checkRedisState.isRedisAvailable()")
    public AuthResponseDto findCustomerUsername(UUID userId) {

        Customer findCustomer = customerRepository.findById(userId).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.toDto(findCustomer);

    }

    /**
     * 고객 단 건 조회, 전체 허용
     *
     * @param customerId
     * @param userId
     * @param role
     * @return
     */
    @Cacheable(cacheNames = "CustomerCache", key = "#customerId", condition = "@checkRedisState.isRedisAvailable()")
    public AuthResponseDto findCustomerById(UUID customerId, UUID userId, String role) {

        // 고객 권한일 때 본인 정보가 아니면 에러
        if (role.equals(UserRole.CUSTOMER.name())) {
            if (!customerId.equals(userId)) {
                throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
            }
        }

        Customer findCustomer = customerRepository.findById(customerId).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.toDto(findCustomer);
    }

    /**
     * 직원 단 건 조회, 전체 허용
     *
     * @param employeeId
     * @param userId
     * @param role
     * @return
     */
    @Cacheable(cacheNames = "EmployeeCache", key = "#employeeId", condition = "@checkRedisState.isRedisAvailable()")
    public AuthResponseDto findEmployeeById(UUID employeeId, UUID userId, String role) {
        
        // 매니저 권한일 때 본인 정보가 아니면 에러
        if (role.equals(UserRole.MANAGER.name())) {
            if (!employeeId.equals(userId)) {
                throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
            }    
        }

        Employee findEmployee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.toDto(findEmployee);
    }

    /**
     * 고객 정보 수정, 모두 허용
     * 고객 : 본인 정보만 수정 가능
     * @param customerId
     * @param request
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    @CachePut(cacheNames = "CustomerCache", key = "#customerId", condition = "@checkRedisState.isRedisAvailable()")
    @CacheEvict(cacheNames = "CustomerSearchCache", allEntries = true)
    public AuthResponseDto updateCustomer(UUID customerId, AuthRequestDto request, UUID userId, String role) {

        // 고객 권한일 때 본인 정보가 아니면 에러
        if (role.equals(UserRole.CUSTOMER.name())) {
            if (!customerId.equals(userId)) {
                throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
            }
        }

        // 고객 존재 유뮤 확인
        Customer customer = customerRepository.findById(customerId).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        // 고객 수정
        if (request.getPassword() != null) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        Customer updateCustomer = customer.update(request);

        return AuthResponseDto.toDto(updateCustomer);
    }

    /**
     * 직원 정보 수정, 마스터 매니저 허용
     * 매니저 : 본인 정보만 수정 가능
     * @param employeeId
     * @param request
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    @CachePut(cacheNames = "EmployeeCache", key = "#employeeId", condition = "@checkRedisState.isRedisAvailable()")
    @CacheEvict(cacheNames = "EmployeeSearchCache", allEntries = true)
    public AuthResponseDto updateEmployee(UUID employeeId, AuthRequestDto request, UUID userId, String role) {

        // 매니저 권한일 때 본인 정보가 아니면 에러
        if (role.equals(UserRole.MANAGER.name())) {
            if (!employeeId.equals(userId)) {
                throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
            }
        }

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        // 직원 수정
        if (request.getPassword() != null) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        Employee updateEmployee = employee.update(request);

        return AuthResponseDto.toDto(updateEmployee);
    }

    /**
     * 고객 전체 조회, 마스터 매니저 허용
     * @param condition
     * @param pageable
     * @return
     */
    @Cacheable(cacheNames = "CustomerSearchCache", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort, #condition.hashCode()}", condition = "@checkRedisState.isRedisAvailable()")
    public Page<AuthResponseDto> findAllCustomer(Pageable pageable, SearchRequestDto condition) {
        return customerRepository.findPagingAllCustomer(pageable, condition);
    }

    /**
     * 직원 전체 조회, 마스터 매니저 허용
     * @param pageable
     * @param condition
     * @return
     */
    @Cacheable(cacheNames = "EmployeeSearchCache", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort, #condition.hashCode()}", condition = "@checkRedisState.isRedisAvailable()")
    public Page<AuthResponseDto> findAllEmployee(Pageable pageable, SearchRequestDto condition) {
        return employeeRepository.findPagingAllEmployee(pageable, condition);
    }

    /**
     * 슬랙 오류로 인한 회원가입 롤백
     * @param request
     * @return
     */
    @Transactional
    public void rollbackSignUp(NotificationRequestDto request) {
        if (UserRole.MASTER.equals(request.getRole()) || UserRole.MANAGER.equals(request.getRole())) {

            // 직원 조회
            Employee findEmployee = employeeRepository.findById(request.getUserId()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            // 직원 삭제
            employeeRepository.delete(findEmployee);

        } else {

            // 고객 조회
            Customer findCustomer = customerRepository.findById(request.getUserId()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            // 고객 삭제
            customerRepository.delete(findCustomer);
        }

    }

    /**
     * 로그인 시도 횟수 +1 증가
     * 계정 잠금 활성화
     * @param username
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateLoginAttempts(String username) {

        Customer findCustomer = customerRepository.findByUsername(username).orElseThrow(() -> new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        findCustomer.loginAttempsCount();
        if (findCustomer.getLoginAttempts() >= 6) {
            findCustomer.accountLock();
        }
    }
}

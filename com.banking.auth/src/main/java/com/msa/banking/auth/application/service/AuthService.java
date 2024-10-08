package com.msa.banking.auth.application.service;

import com.msa.banking.auth.application.jwt.JwtUtil;
import com.msa.banking.auth.domain.model.BlackListToken;
import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.auth.infrastructure.repository.BlackListTokenRepository;
import com.msa.banking.auth.infrastructure.repository.CustomerRepository;
import com.msa.banking.auth.infrastructure.repository.EmployeeRepository;
import com.msa.banking.auth.presentation.request.AuthSignInRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignUpRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.NotiType;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BlackListTokenRepository blackListTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    /**
     * 회원가입
     * @param request
     * @return
     */
    @Transactional
    public AuthResponseDto createAuth(AuthSignUpRequestDto request) {

        // 직원 회원가입
        if (UserRole.valueOf(request.getRole()) == UserRole.MASTER || UserRole.valueOf(request.getRole()) == UserRole.MANAGER) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            Employee employee = AuthSignUpRequestDto.toEmployee(request);

            // 유저 ID 유니크 제약 조건 검증
            if (existsEmployeeByUsername(request.getUsername())) {
                throw new GlobalCustomException(ErrorCode.USERNAME_DUPLICATE_RESOURCES);
            }

            // Email 유니크 제약 조건 검증
            if (existsEmployeeByEmail(request.getEmail())) {
                throw new GlobalCustomException(ErrorCode.EMAIL_DUPLICATE_RESOURCES);
            }

            // PhoneNumber 유니크 제약 조건 검증
            if (existsEmployeeByPhoneNumber(request.getPhoneNumber())) {
                throw new GlobalCustomException(ErrorCode.PHONE_NUMBER_DUPLICATE_RESOURCES);
            }

            // 직원 저장
            Employee savedEmployee = employeeRepository.save(employee);

            // kafka 알림 회원 가입 메세지 보내기
            // 트랜잭션이 커밋된 후에 Kafka 메시지를 전송
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    NotificationRequestDto notifyDto = new NotificationRequestDto(
                            savedEmployee.getId(),
                            savedEmployee.getSlackId(),
                            savedEmployee.getRole(),
                            NotiType.SIGNUP,
                            savedEmployee.getName() + "님 회원 가입을 축하드립니다."
                    );
                    signUpNotify(notifyDto);
                }
            });

            return AuthResponseDto.toDto(savedEmployee);


        } else { // 고객 회원가입
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            Customer customer = AuthSignUpRequestDto.toCustomer(request);

            // 유저 ID 유니크 제약 조건 검증
            if (existsCustomerByUsername(request.getUsername())) {
                throw new GlobalCustomException(ErrorCode.USERNAME_DUPLICATE_RESOURCES);
            }

            // Email 유니크 제약 조건 검증
            if (existsCustomerByEmail(request.getEmail())) {
                throw new GlobalCustomException(ErrorCode.EMAIL_DUPLICATE_RESOURCES);
            }

            // PhoneNumber 유니크 제약 조건 검증
            if (existsCustomerByPhoneNumber(request.getPhoneNumber())) {
                throw new GlobalCustomException(ErrorCode.PHONE_NUMBER_DUPLICATE_RESOURCES);
            }

            // 고객 저장
            Customer savedCustomer = customerRepository.save(customer);

            // kafka 알림 회원 가입 메세지 보내기
            // 트랜잭션이 커밋된 후에 Kafka 메시지를 전송
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    NotificationRequestDto notifyDto = new NotificationRequestDto(
                            savedCustomer.getId(),
                            savedCustomer.getSlackId(),
                            savedCustomer.getRole(),
                            NotiType.SIGNUP,
                            savedCustomer.getName() + "님 회원 가입을 축하드립니다."
                    );
                    signUpNotify(notifyDto);
                }
            });

            return AuthResponseDto.toDto(savedCustomer);
        }
    }

    /**
     * 로그인
     * @param request
     * @return
     */
    public String signInAuth(AuthSignInRequestDto request, HttpServletResponse response) {

        if (request.getRole().equals(UserRole.MASTER.name()) || request.getRole().equals(UserRole.MANAGER.name())) {

            // 가입 여부 확인
            Employee findEmployee = employeeRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            // 비밀번호 일치 확인
            if (!passwordEncoder.matches(request.getPassword(), findEmployee.getPassword())) {
                throw new GlobalCustomException(ErrorCode.PASSWORD_BAD_REQUEST);
            }


            String token = jwtUtil.createToken(findEmployee.getId().toString(), findEmployee.getUsername(), findEmployee.getRole().name());

            // 응답 헤더에 토큰 추가
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            return "login successful";
        } else {

            // 가입 여부 확인
            Customer findCustomer = customerRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            // 비밀번호 일치 확인
            if (!passwordEncoder.matches(request.getPassword(), findCustomer.getPassword())) {
                throw new GlobalCustomException(ErrorCode.PASSWORD_BAD_REQUEST);
            }

            String token = jwtUtil.createToken(findCustomer.getId().toString(), findCustomer.getUsername(), findCustomer.getRole().name());

            // 응답 헤더에 토큰 추가
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            return "login successful";
        }
    }

    /**
     *  회원 가입 메시지 보내기 | kafka
     * @param notifyDto
     */
    public void signUpNotify(NotificationRequestDto notifyDto) {
        kafkaTemplate.send(Topic.SIGN_UP.getTopic(), EventSerializer.serialize(notifyDto));
    }

    /**
     * 직원 username 유니크 제약 조건 검증
     * @param username
     * @return
     */
    public boolean existsEmployeeByUsername(String username) {
        boolean b = employeeRepository.existsByUsername(username);
        System.out.println(b);
        return b;
    }

    /**
     * 고객 username 유니크 제약 조건 검증
     * @param username
     * @return
     */
    public boolean existsCustomerByUsername(String username) {
        boolean b = customerRepository.existsByUsername(username);
        System.out.println(b);
        return b;
    }

    /**
     * 직원 email 유니크 제약 조건 검증
     * @param email
     * @return
     */
    public boolean existsEmployeeByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    /**
     * 고객 email 유니크 제약 조건 검증
     * @param email
     * @return
     */
    public boolean existsCustomerByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * 직원 phoneNumber 유니크 제약 조건 검증
     * @param phoneNumber
     * @return
     */
    public boolean existsEmployeeByPhoneNumber(String phoneNumber) {
        return employeeRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * 고객 phoneNumber 유니크 제약 조건 검증
     * @param phoneNumber
     * @return
     */
    public boolean existsCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * 블랙리스트 토큰 저장
     * @param request
     */
    @Transactional
    public void logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // JWT 토큰의 만료 시간 추출 (JWT 파싱 로직에 따라 구현)
            long expiration = getExpirationTimeFromJwt(token);

            // Redis에 토큰을 블랙리스트로 추가, 만료 시간 동안 저장
            redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);

            // db에 토큰을 블랙리스트로 추가
            BlackListToken blackListToken = BlackListToken.create(token, LocalDateTime.now().plusNanos(expiration * 1_000_000));

            blackListTokenRepository.save(blackListToken);

            log.info("JWT 토큰이 블랙리스트에 추가되었습니다: " + token);
        } else {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 토큰 남은 시간 추출
     * @param token
     * @return
     */
    private long getExpirationTimeFromJwt(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}

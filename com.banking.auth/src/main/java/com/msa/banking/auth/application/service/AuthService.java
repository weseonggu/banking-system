package com.msa.banking.auth.application.service;

import com.msa.banking.auth.application.client.NotificationClient;
import com.msa.banking.auth.application.jwt.JwtUtil;
import com.msa.banking.auth.domain.model.*;
import com.msa.banking.auth.infrastructure.repository.BlackListTokenRepository;
import com.msa.banking.auth.infrastructure.repository.CustomerRepository;
import com.msa.banking.auth.infrastructure.repository.EmployeeRepository;
import com.msa.banking.auth.infrastructure.repository.SlackCodeRepository;
import com.msa.banking.auth.presentation.request.AuthResetPasswordRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignInRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignUpRequestDto;
import com.msa.banking.auth.presentation.request.SlackNumberRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.auth.dto.SlackIdRequestDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.NotiType;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final UserService userService;
    private final NotificationClient notificationClient;
    private final SlackCodeRepository slackCodeRepository;

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

            // 슬랙 검증 통과 여부 확인
            SlackCode findSlackCode = slackCodeRepository.findBySlackIdAndIsValid(request.getSlackId(), true).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.SLACK_NOT_VALID));

            // 검증 완료 시 삭제
            if (findSlackCode != null) {
                slackCodeRepository.deleteBySlackIdAndIsValid(request.getSlackId());
            }

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

            // 슬랙 검증 통과 여부 확인
            SlackCode findSlackCode = slackCodeRepository.findBySlackIdAndIsValid(request.getSlackId(), true).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.SLACK_NOT_VALID));
            
            // 검증 완료 시 삭제
            if (findSlackCode != null) {
                slackCodeRepository.deleteBySlackIdAndIsValid(request.getSlackId());
            }

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
    @Transactional
    public String signInAuth(AuthSignInRequestDto request, HttpServletResponse response) {

        // 직원 로그인
        if (request.getRole().equals(UserRole.MASTER.name()) || request.getRole().equals(UserRole.MANAGER.name())) {

            // 가입 여부 확인
            Employee findEmployee = employeeRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            // 비밀번호 일치 확인
            if (!passwordEncoder.matches(request.getPassword(), findEmployee.getPassword())) {
                throw new GlobalCustomException(ErrorCode.EMPLOYEE_PASSWORD_BAD_REQUEST);
            }


            String token = jwtUtil.createToken(findEmployee.getId().toString(), findEmployee.getUsername(), findEmployee.getRole().name());

            // 응답 헤더에 토큰 추가
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            return "login successful";

        } else { // 회원 로그인

            // 가입 여부 확인
            Customer findCustomer = customerRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

            // 비밀번호 6회 이상 오류로 계정 잠김 에러
            if (findCustomer.isAccountLock()) {
                throw new GlobalCustomException(ErrorCode.ACCOUNT_LOCKED);
            }

            // 비밀번호 일치 확인 | 틀렸을 경우 로그인 시도 횟수 증가 | 6회 이상 틀렸을 경우 계정 잠금
            if (!passwordEncoder.matches(request.getPassword(), findCustomer.getPassword())) {
                // 로그인 시도 횟수 +1 증가 | 계정 잠금 활성화
                userService.updateLoginAttempts(request.getUsername());


                throw new GlobalCustomException(ErrorCode.CUSTOMER_PASSWORD_BAD_REQUEST);

            }

            // 로그인 성공 시 시도 횟수 초기화
            findCustomer.resetLoginAttemps();

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

    /**
     * 고객 비밀번호 초기화
     * @param request
     */
    @Transactional
    public void resetPassword(AuthResetPasswordRequestDto request) {

        // 고객 조회
        Customer findCustomer = customerRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                new GlobalCustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 인코딩
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        // 비밀번호 변경 및 계정 잠금 비활성화
        findCustomer.updatePassword(request.getPassword());

    }

    /**
     * 슬랙 인증 번호 발송
     * @param request
     * @return
     */
    @Transactional
    public String slackCheck(SlackIdRequestDto request) {
        // 레디스 다운 시 처리 될 플래그
        boolean isRedisAvailable = true;

        // 6자리 인증번호 생성
        String slackNumber = null;
        try {
            slackNumber = notificationClient.slackCheck(request);
        } catch (FeignException.BadRequest e) {
            log.error("슬랙 인증 번호 발송 에러 :{}", e.getMessage());
            throw new GlobalCustomException(ErrorCode.SLACK_ERROR);
        }

        // Redis 키 확인 및 삭제 후 새로 생성
        try {
            if (redisTemplate.hasKey(request.getSlackId())) {
                log.info("기존 Redis, DB 키 삭제: {}", request.getSlackId());
                slackCodeRepository.deleteBySlackId(request.getSlackId());
                redisTemplate.delete(request.getSlackId());
            }
        } catch (RedisConnectionFailureException e) {
            log.error("Redis Connection 에러: {}", e.getMessage());
            log.info("기존 데이터 DB 존재 여부 확인 후 삭제: {}", request.getSlackId());
            isRedisAvailable = false;
            slackCodeRepository.deleteBySlackId(request.getSlackId());
        }


        // Redis에 5분(300초) 만료 시간으로 인증번호 저장
        if (isRedisAvailable) {
            redisTemplate.opsForValue().set(request.getSlackId(), slackNumber, 300, TimeUnit.SECONDS);
        }

        // 슬랙코드 객체 생성
        SlackCode slackCode = SlackCode.createSlackCode(request.getSlackId(), slackNumber, LocalDateTime.now().plusMinutes(5));
    
        // DB 저장
        slackCodeRepository.save(slackCode);

        return "5분 후 인증번호는 만료됩니다.";
    }

    /**
     * 슬랙 인증 번호 검증
     * @param request
     * @return
     */
    @Transactional
    public String slackCheckValid(SlackNumberRequestDto request) {

        String slackId = request.getSlackId();
        String slackNumber = request.getSlackNumber();

        try {
            // 키가 존재 할 경우
            if (redisTemplate.hasKey(slackId)) {
                // Redis에서 slackId 키에 대한 값을 가져옴
                String storedSlackNumber = (String) redisTemplate.opsForValue().get(slackId);

                // 인증 번호 값이 같을 때 데이터 검증 true 변경 후 리턴
                if (slackNumber.equals(storedSlackNumber)) {
                    redisTemplate.delete(slackId);

                    // 검증이 되지 않았는지 확인
                    SlackCode findSlackCode = slackCodeRepository.findBySlackIdAndIsValid(request.getSlackId(), false).orElseThrow(() ->
                            new GlobalCustomException(ErrorCode.SLACK_ERROR));

                    // 검증이 된 것이 있다면 삭제
                    boolean slackValid = slackCodeRepository.existsBySlackIdAndIsValid(request.getSlackId(), true);
                    if (slackValid) {
                        slackCodeRepository.deleteBySlackIdAndIsValid(request.getSlackId());
                    }

                    findSlackCode.changeIsValid();
                    return "슬랙 인증 번호 검증 성공. 회원가입을 진행 해주세요.";

                } else { // 인증 번호 값이 다를 때
                    log.error("슬랙 인증번호 불일치 | slackNumber: {}, storedSlackNumber: {}", slackNumber, storedSlackNumber);
                    throw new GlobalCustomException(ErrorCode.SLACK_VERIFICATION_CODE_ERROR);
                }

            } else { // 키가 존재하지 않을 경우
                log.error("키가 존재하지 않거나 만료됨 | slackId: {}",slackId);
                throw new GlobalCustomException(ErrorCode.SLACK_VERIFICATION_CODE_NOT_FOUND);
            }
        } catch (RedisConnectionFailureException e) { // 레디스 다운 시
            // DB 에서 slackId 로 조회
            SlackCode slackCode = slackCodeRepository.findBySlackId(request.getSlackId()).orElseThrow(() ->
                    new GlobalCustomException(ErrorCode.SLACK_VERIFICATION_CODE_ERROR));

            // 승인 번호 일치 시 db 삭제 후 반환
            if (slackCode.getSlackCode().equals(request.getSlackNumber())) {
                slackCodeRepository.deleteBySlackId(request.getSlackId());
                return "슬랙 인증 번호 검증 성공. 회원가입을 진행 해주세요.";
            }else { // 승인번호 불일치
                log.error("슬랙 인증번호 불일치 | slackNumber: {}, db.slackCode: {}", slackNumber, slackCode.getSlackCode());
                throw new GlobalCustomException(ErrorCode.SLACK_VERIFICATION_CODE_ERROR);
            }
        }
    }
}

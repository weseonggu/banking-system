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
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.NotiType;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
            Employee savedEmployee = null;

            try {
                savedEmployee = employeeRepository.save(employee);
            } catch (ConstraintViolationException e) {
                throw new GlobalCustomException(ErrorCode.DUPLICATE_RESOURCE);
            }

            // kafka 알림 회원 가입 메세지 보내기
            // 트랜잭션이 커밋된 후에 Kafka 메시지를 전송
            Employee finalSavedEmployee = savedEmployee;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    NotificationRequestDto notifyDto = new NotificationRequestDto(
                            finalSavedEmployee.getId(),
                            finalSavedEmployee.getSlackId(),
                            finalSavedEmployee.getRole(),
                            NotiType.SIGNUP,
                            finalSavedEmployee.getName() + "님 회원 가입을 축하드립니다."
                    );
                    signUpNotify(notifyDto);
                }
            });

            return AuthResponseDto.toDto(savedEmployee);


        }else { // 고객 회원가입
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            Customer customer = AuthSignUpRequestDto.toCustomer(request);
            Customer savedCustomer = null;

            try {
                savedCustomer = customerRepository.save(customer);
            } catch (ConstraintViolationException e) {
                throw new GlobalCustomException(ErrorCode.DUPLICATE_RESOURCE);
            }

            // kafka 알림 회원 가입 메세지 보내기
            // 트랜잭션이 커밋된 후에 Kafka 메시지를 전송
            Customer finalsavedCustomer = savedCustomer;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    NotificationRequestDto notifyDto = new NotificationRequestDto(
                            finalsavedCustomer.getId(),
                            finalsavedCustomer.getSlackId(),
                            finalsavedCustomer.getRole(),
                            NotiType.SIGNUP,
                            finalsavedCustomer.getName() + "님 회원 가입을 축하드립니다."
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
}

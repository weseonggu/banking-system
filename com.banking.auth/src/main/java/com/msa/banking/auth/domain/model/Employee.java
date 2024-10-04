package com.msa.banking.auth.domain.model;

import com.msa.banking.auth.presentation.request.AuthRequestDto;
import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_employee")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_delete = false")
@Getter
public class Employee extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "employee_id")
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "slack_id", nullable = false)
    private String slackId;

    public Employee(String username, String password, String name, String email, String phoneNumber, Address address, UserRole role, String slackId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.slackId = slackId;
    }

    /**
     * 생성 메서드
     * @param username
     * @param password
     * @param name
     * @param email
     * @param phoneNumber
     * @param address
     * @param role
     * @return
     */
    public static Employee createEmployee(String username, String password, String name, String email, String phoneNumber, Address address, UserRole role, String slackId) {
        return new Employee(username, password, name, email, phoneNumber, address, role, slackId);
    }

    /**
     * 직원 정보 수정 메서드
     * @param request
     * @return
     */
    public Employee update(AuthRequestDto request) {
        String city = request.getCity();
        String street = request.getStreet();
        String zipcode = request.getZipcode();

        // 모든 값이 null 아닌지 확인
        if (city == null && street == null && zipcode == null) {
            // 주소 필드가 모두 null이면 기존 주소 값을 그대로 유지

        } else if (city != null && street != null && zipcode != null) {
            // 주소 필드가 모두 존재할 경우에만 업데이트
            this.address.updateAddress(city, street, zipcode);

        } else {
            // 주소 필드 중 하나라도 null이거나 불완전할 경우 예외 처리
            throw new GlobalCustomException(ErrorCode.ADDRESS_BAD_REQUEST);
        }

        if (request.getUsername() != null) {
            this.username = request.getUsername();
        }

        if (request.getPassword() != null) {
            this.password = request.getPassword();
        }

        if (request.getName() != null) {
            this.name = request.getName();
        }

        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }

        if (request.getPhoneNumber() != null) {
            this.phoneNumber = request.getPhoneNumber();
        }

        if (request.getSlackId() != null) {
            this.slackId = request.getSlackId();
        }

        return this;
    }
}

package com.msa.banking.auth.presentation.request;

import com.msa.banking.auth.domain.model.Address;
import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.common.base.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignUpRequestDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    @Size(min = 4, max = 10, message = "아이디는 최소 4자 이상, 10자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 알파벳 소문자와 숫자로만 구성되어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수 입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입니다.")
    @Email(message = "이메일 형식이 유효하지 않습니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(
            regexp = "^010-\\d{3,4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다."
    )
    private String phoneNumber;

    @NotBlank(message = "도시명은 필수 입니다.")
    private String city;

    @NotBlank(message = "도로명은 필수 입니다.")
    private String street;

    @NotBlank(message = "우편번호는 필수 입니다.")
    private String zipcode;

    @NotBlank(message = "권한은 필수 입니다.")
    @Pattern(regexp = "MASTER|MANAGER|CUSTOMER", message = "권한은 MASTER, MANAGER, CUSTOMER 중 하나여야 합니다.")
    private String role;

    public static Customer toCustomer(AuthSignUpRequestDto dto) {

        Address address = new Address(dto.getCity(), dto.getStreet(), dto.getZipcode());

        return new Customer(dto.getUsername(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                address,
                UserRole.valueOf(dto.getRole()));
    }

    public static Employee toEmployee(AuthSignUpRequestDto dto) {

        Address address = new Address(dto.getCity(), dto.getStreet(), dto.getZipcode());

        return new Employee(dto.getUsername(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                address,
                UserRole.valueOf(dto.getRole()));
    }
}

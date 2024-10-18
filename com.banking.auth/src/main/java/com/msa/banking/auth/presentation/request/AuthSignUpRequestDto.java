package com.msa.banking.auth.presentation.request;

import com.msa.banking.auth.domain.model.Address;
import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.common.base.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,10}$",
            message = "아이디는 영어와 숫자를 조합하여 4자리 이상, 10자리 이하로 작성해야 합니다.")
    @Schema(description = "username", example = "customer1", type = "string")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    @Schema(description = "password", example = "Password123!", type = "string")
    private String password;

    @NotBlank(message = "이름은 필수 입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\s]+$", message = "이름에는 숫자를 포함할 수 없습니다.")
    @Schema(description = "name", example = "유저 하나", type = "string")
    private String name;

    @NotBlank(message = "이메일은 필수 입니다.")
    @Email(message = "이메일 형식이 유효하지 않습니다.")
    @Schema(description = "email", example = "john.doe1@example.com", type = "string")
    private String email;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(
            regexp = "^010-\\d{3,4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다."
    )
    @Schema(description = "phoneNumber", example = "010-1111-1111", type = "string")
    private String phoneNumber;

    @NotBlank(message = "도시명은 필수 입니다.")
    @Schema(description = "city", example = "Seoul", type = "string")
    private String city;

    @NotBlank(message = "도로명은 필수 입니다.")
    @Schema(description = "street", example = "123 Main St", type = "string")
    private String street;

    @NotBlank(message = "우편번호는 필수 입니다.")
    @Schema(description = "zipcode", example = "12345", type = "string")
    private String zipcode;

    @NotBlank(message = "권한은 필수 입니다.")
    @Pattern(regexp = "MASTER|MANAGER|CUSTOMER", message = "권한은 MASTER, MANAGER, CUSTOMER 중 하나여야 합니다.")
    @Schema(description = "role", example = "CUSTOMER", type = "string")
    private String role;

    @NotBlank(message = "슬랙 ID 필수 입니다.")
    @Schema(description = "slackId", example = "U07MFBXHTG9", type = "string")
    private String slackId;

    public static Customer toCustomer(AuthSignUpRequestDto dto) {

        Address address = new Address(dto.getCity(), dto.getStreet(), dto.getZipcode());

        return  Customer.createCustomer(dto.getUsername(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                address,
                UserRole.valueOf(dto.getRole()),
                dto.getSlackId());
    }

    public static Employee toEmployee(AuthSignUpRequestDto dto) {

        Address address = new Address(dto.getCity(), dto.getStreet(), dto.getZipcode());

        return Employee.createEmployee(dto.getUsername(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                address,
                UserRole.valueOf(dto.getRole()),
                dto.getSlackId());
    }
}

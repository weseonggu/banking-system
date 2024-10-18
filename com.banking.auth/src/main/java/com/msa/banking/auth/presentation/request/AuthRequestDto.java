package com.msa.banking.auth.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    @Size(min = 4, max = 10, message = "아이디는 최소 4자 이상, 10자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,10}$",
            message = "아이디는 영어와 숫자를 조합하여 4자리 이상, 10자리 이하로 작성해야 합니다.")
    @Schema(description = "username", example = "customer1", type = "string")
    private String username;

    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    @Schema(description = "password", example = "Password123!", type = "string")
    private String password;

    @Pattern(regexp = "^[a-zA-Z가-힣\\s]+$", message = "이름에는 숫자를 포함할 수 없습니다.")
    @Schema(description = "name", example = "유저 하나", type = "string")
    private String name;

    @Email(message = "이메일 형식이 유효하지 않습니다.")
    @Schema(description = "email", example = "john.doe1@example.com", type = "string")
    private String email;

    @Pattern(
            regexp = "^010-\\d{3,4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다."
    )
    @Schema(description = "phoneNumber", example = "010-1111-1111", type = "string")
    private String phoneNumber;

    @Schema(description = "city", example = "Seoul", type = "string")
    private String city;

    @Schema(description = "street", example = "123 Main St", type = "string")
    private String street;

    @Schema(description = "zipcode", example = "12345", type = "string")
    private String zipcode;

    @Schema(description = "slackId", example = "U07MFBXHTG9", type = "string")
    private String slackId;
}

package com.msa.banking.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignInRequestDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "본인의 권한은 필수 입니다.")
    @Pattern(regexp = "MASTER|MANAGER|CUSTOMER", message = "권한은 MASTER, MANAGER, CUSTOMER 중 하나여야 합니다.")
    private String role;

}

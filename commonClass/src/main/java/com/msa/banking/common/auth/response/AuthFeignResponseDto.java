package com.msa.banking.common.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthFeignResponseDto {

    private UUID id;

    private String username;

    private String name;
    private String email;
    private String phoneNumber;

    private String city;
    private String street;
    private String zipcode;

    private String role;
    private String slackId;

}

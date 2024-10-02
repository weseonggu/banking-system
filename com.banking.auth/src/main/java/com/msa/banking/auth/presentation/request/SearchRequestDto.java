package com.msa.banking.auth.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {

    // equal 비교 필드
    private UUID userId;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    
    // contains 비교 필드
    private String city;
    private String street;
    private String zipcode;
    
}

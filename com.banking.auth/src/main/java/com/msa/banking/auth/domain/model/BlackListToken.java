package com.msa.banking.auth.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_balack_list_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlackListToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;
    private LocalDateTime expiration;

    public BlackListToken(String token, LocalDateTime expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public static BlackListToken create(String token, LocalDateTime expiration) {
        return new BlackListToken(token, expiration);
    }
}

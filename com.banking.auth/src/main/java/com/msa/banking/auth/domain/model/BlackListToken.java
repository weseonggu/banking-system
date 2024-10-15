package com.msa.banking.auth.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_black_list_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlackListToken {

    @Id
    @GeneratedValue
    @Column(name = "black_list_token_id")
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiration;

    public BlackListToken(String token, LocalDateTime expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public static BlackListToken create(String token, LocalDateTime expiration) {
        return new BlackListToken(token, expiration);
    }
}

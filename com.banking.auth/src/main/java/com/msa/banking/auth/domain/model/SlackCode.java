package com.msa.banking.auth.domain.model;

import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_slack_code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_delete = false")
@Getter
public class SlackCode extends AuditEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "slack_id", nullable = false)
    private String slackId;

    @Column(name = "slack_code", nullable = false)
    private String slackCode;

    @Column(name = "expires_time", nullable = false)
    private LocalDateTime expiresTime;

    @Column(name = "is_valid")
    private boolean isValid = false;

    public SlackCode(String slackId, String slackCode, LocalDateTime expiresTime) {
        this.slackId = slackId;
        this.slackCode = slackCode;
        this.expiresTime = expiresTime;
    }

    /**
     * 슬랙 코드 생성 메서드
     * @param slackId
     * @param slackCode
     * @param expiresTime
     * @return
     */
    public static SlackCode createSlackCode(String slackId,String slackCode, LocalDateTime expiresTime) {
        return new SlackCode(slackId, slackCode, expiresTime);
    }

    /**
     * 인증 성공 시 true 변환
     */
    public void changeIsValid() {
        this.isValid = true;
    }
}

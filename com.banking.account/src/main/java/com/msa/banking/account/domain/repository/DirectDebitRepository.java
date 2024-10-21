package com.msa.banking.account.domain.repository;

import com.msa.banking.account.application.dto.SecondBatchDirectDebitResponseDto;
import com.msa.banking.account.domain.model.DirectDebit;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface DirectDebitRepository extends JpaRepository<DirectDebit, UUID>, DirectDebitRepositoryCustom {

    // 특정 날짜에 해당하는 계좌 아이디와 송금 계좌, 금액 가져오기
    @Query("select new com.msa.banking.account.application.dto.SecondBatchDirectDebitResponseDto(d.account.accountId, d.beneficiaryAccount, d.amount) "
            + "from DirectDebit d where d.transferDate = :transferDate")
    List<SecondBatchDirectDebitResponseDto> findByTransferDate(@Param("transferDate") Integer transferDate);
}

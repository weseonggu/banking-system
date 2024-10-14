package com.msa.banking.account.presentation.dto.transactions;

public record TransferTransactionResponseDto(

        SenderTransactionResponseDto senderTransaction,
        BeneficiaryTransactionResponseDto beneficiaryTransaction
) {
}

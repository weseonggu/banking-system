package com.msa.banking.account.application.mapper;

import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.personal.PersonalHistoryType;

public class EnumMapper {

    // transactionType -> PersonalHistoryType 변환 메서드
    public static PersonalHistoryType toPersonalHistoryType(TransactionType transactionType) {
        switch (transactionType) {
            case DEPOSIT:
                return PersonalHistoryType.DEPOSIT;
            case WITHDRAWAL:
                return PersonalHistoryType.WITHDRAWAL;
            case TRANSFER:
                return PersonalHistoryType.TRANSFER;
            case PAYMENT:
                return PersonalHistoryType.PAYMENT;
            case SAVINGS_DEPOSIT:
                return PersonalHistoryType.SAVINGS_DEPOSIT;
            case LOAN_DEPOSIT:
                return PersonalHistoryType.LOAN_DEPOSIT;
            default:
                throw new IllegalArgumentException("Unsupported transactionType: " + transactionType);
        }
    }
}

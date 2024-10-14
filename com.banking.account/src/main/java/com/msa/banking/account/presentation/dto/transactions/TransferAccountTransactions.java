package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.AccountTransactions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferAccountTransactions {

    private AccountTransactions sender;
    private AccountTransactions receiver;
}

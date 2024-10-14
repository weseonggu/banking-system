package com.msa.banking.account.application.event;

import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.common.account.type.TransactionStatus;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransactionStatusEventListener {

    private final TransactionsRepository transactionsRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCommit(TransactionStatusEvent event) {
        Long transactionId = event.transactionId();

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        transaction.updateTransactionStatus(TransactionStatus.COMPLETED);
        transactionsRepository.save(transaction);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleTransactionRollback(TransactionStatusEvent event){
        Long transactionId = event.transactionId();

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        transaction.updateTransactionStatus(TransactionStatus.FAILED);
        transactionsRepository.save(transaction);
    }
}

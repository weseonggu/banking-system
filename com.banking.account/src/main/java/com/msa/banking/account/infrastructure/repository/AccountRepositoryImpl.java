package com.msa.banking.account.infrastructure.repository;


import com.msa.banking.account.application.mapper.AccountMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.common.account.type.AccountType;
import com.msa.banking.account.domain.repository.AccountRepositoryCustom;
import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import com.msa.banking.account.presentation.dto.account.AccountSearchRequestDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.msa.banking.account.domain.model.QAccount.account;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final AccountMapper accountMapper;

    @Override
    public Page<AccountListResponseDto> searchAccounts(AccountSearchRequestDto search, Pageable pageable) {


        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        long total = queryFactory
                .selectFrom(account)
                .where(
                        accountNumberMatches(search.getAccountNumber()),
                        accountHolderMatches(search.getAccountHolder()),
                        accountTypeMatches(search.getType())
                )
                .fetch()
                .size();


        List<Account> results = queryFactory
                .selectFrom(account)
                .where(
                        accountNumberMatches(search.getAccountNumber()),
                        accountHolderMatches(search.getAccountHolder()),
                        accountTypeMatches(search.getType())
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // MapStruct를 이용하여 toListDtos와 같은 메서드로 리스트를 변환하는 방식과,
        // stream을 사용하여 직접 처리하는 방식 간에 성능 차이는 미미하거나 거의 없다.
        List<AccountListResponseDto> content = accountMapper.toListDtos(results);
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression accountNumberMatches(String accountNumber) {
        return accountNumber != null ? account.accountNumber.eq(accountNumber) : null;
    }

    private BooleanExpression accountHolderMatches(String accountHolder) {
        return accountHolder != null ? account.accountHolder.eq(accountHolder) : null;
    }

    private BooleanExpression accountTypeMatches(AccountType accountType) {
        return accountType != null ? account.type.eq(accountType) : null;
    }


//    결과 순서
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "createdAt" :
                        orders.add(new OrderSpecifier<>(direction, account.createdAt));
                        break;
                    case "type" :
                        orders.add(new OrderSpecifier<>(direction, account.type));
                        break;
                    case "accountNumber" :
                        orders.add(new OrderSpecifier<>(direction, account.accountNumber));
                        break;
                }
            }
        }
        return orders;
    }
}

package com.msa.banking.account.infrastructure.repository;

import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.model.TransactionType;
import com.msa.banking.account.domain.repository.TransactionsRepositoryCustom;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.msa.banking.account.domain.model.QAccountTransactions.accountTransactions;

@Repository
@RequiredArgsConstructor
public class TransactionsRepositoryImpl implements TransactionsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final TransactionsMapper transactionsMapper;


    @Override
    public Page<TransactionsListResponseDto> searchTransactions(TransactionsSearchRequestDto search, Pageable pageable) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        long total = queryFactory
                .selectFrom(accountTransactions)
                .where(
                        typeMatches(search.getType()),
                        originatingAccountMatches(search.getAccountNumber()),
                        searchDateContains(search.getSearchDate())
                )
                .fetch()
                .size();

        List<AccountTransactions> results = queryFactory
                .selectFrom(accountTransactions)
                .where(
                        typeMatches(search.getType()),
                        originatingAccountMatches(search.getAccountNumber()),
                        searchDateContains(search.getSearchDate())
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<TransactionsListResponseDto> content = transactionsMapper.toListDtos(results);
        return new PageImpl<>(content, pageable, total);
    }


    private BooleanExpression typeMatches(TransactionType type) {
        return type != null ? accountTransactions.type.eq(type) : null;
    }

    private BooleanExpression originatingAccountMatches(String originatingAccount) {
        return originatingAccount != null ? accountTransactions.originatingAccount.eq(originatingAccount) : null;
    }

    private BooleanExpression searchDateContains(LocalDate searchDate) {

        if (searchDate == null) return null;

        LocalDateTime startOfDay = searchDate.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = searchDate.atTime(23, 59, 59); // 23:59:5
        return accountTransactions.createdAt.between(startOfDay, endOfDay);
    }

    // 결과 순서
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "type" :
                        orders.add(new OrderSpecifier<>(direction, accountTransactions.type));
                        break;
                    case "originatingAccount" :
                        orders.add(new OrderSpecifier<>(direction, accountTransactions.originatingAccount));
                        break;
                    case "createdAt" :
                        orders.add(new OrderSpecifier<>(direction, accountTransactions.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}
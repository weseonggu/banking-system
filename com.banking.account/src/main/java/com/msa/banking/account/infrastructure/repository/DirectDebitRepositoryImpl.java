package com.msa.banking.account.infrastructure.repository;

import com.msa.banking.account.application.mapper.DirectDebitMapper;
import com.msa.banking.account.domain.model.DirectDebit;
import com.msa.banking.account.domain.repository.DirectDebitRepositoryCustom;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitListResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitSearchRequestDto;
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

import static com.msa.banking.account.domain.model.QDirectDebit.directDebit;

@Repository
@RequiredArgsConstructor
public class DirectDebitRepositoryImpl implements DirectDebitRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final DirectDebitMapper directDebitMapper;


    @Override
    public Page<DirectDebitListResponseDto> searchDirectDebits(DirectDebitSearchRequestDto search, Pageable pageable) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        long total = queryFactory
                .selectFrom(directDebit)
                .where(
                        originatingAccountMatches(search.getOriginatingAccount()),
                        beneficiaryAccountMatches(search.getBeneficiaryAccount())
                )
                .fetch()
                .size();

        List<DirectDebit> results = queryFactory
                .selectFrom(directDebit)
                .where(
                        originatingAccountMatches(search.getOriginatingAccount()),
                        beneficiaryAccountMatches(search.getBeneficiaryAccount())
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DirectDebitListResponseDto> content = directDebitMapper.toListDtos(results);
        return new PageImpl<>(content, pageable, total);
    }


    private BooleanExpression originatingAccountMatches(String originatingAccount) {
        return originatingAccount != null ? directDebit.account.accountNumber.eq(originatingAccount) : null;
    }

    private BooleanExpression beneficiaryAccountMatches(String beneficiaryAccount) {
        return beneficiaryAccount != null ? directDebit.beneficiaryAccount.eq(beneficiaryAccount) : null;
    }

    // 결과 순서
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "originatingAccount" :
                        orders.add(new OrderSpecifier<>(direction, directDebit.account.accountNumber));
                        break;
                    case "createdAt" :
                        orders.add(new OrderSpecifier<>(direction, directDebit.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}
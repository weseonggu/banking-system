package com.msa.banking.personal.infrastructure.persistence;

import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.model.QPersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PersonalHistoryRepositoryCustomImpl implements PersonalHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PersonalHistoryRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PersonalHistory> findByCategoryAndStatus(String categoryName, Boolean status, Pageable pageable) {
        QPersonalHistory personalHistory = QPersonalHistory.personalHistory;

        // QueryDSL을 이용한 검색
        List<PersonalHistory> results = queryFactory
                .selectFrom(personalHistory)
                .where(
                        categoryNameEq(categoryName),
                        statusEq(status)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수를 가져와서 페이징된 결과와 함께 반환
        long total = queryFactory
                .selectFrom(personalHistory)
                .where(
                        categoryNameEq(categoryName),
                        statusEq(status)
                )
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression categoryNameEq(String categoryName) {
        return categoryName != null ? QPersonalHistory.personalHistory.category.name.eq(categoryName) : null;
    }

    private BooleanExpression statusEq(Boolean status) {
        return status != null ? QPersonalHistory.personalHistory.status.eq(status) : null;
    }
}

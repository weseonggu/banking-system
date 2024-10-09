package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.application.dto.QUsingProductPage;
import com.msa.banking.product.application.dto.UsingProductPage;
import com.msa.banking.product.domain.repository.UsingProductRepositoryCustom;
import com.msa.banking.product.presentation.request.RequestUsingProductConditionDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static com.msa.banking.product.domain.model.QUsingProduct.usingProduct;

public class UsingProductRepositoryImpl implements UsingProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UsingProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<UsingProductPage> findAllUsingProductPages(Pageable page, RequestUsingProductConditionDto condition) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(userIdEq(condition.getUserid()))
                .and(name(condition.getName()));


        JPAQuery<UsingProductPage> query = queryFactory
                .select(new QUsingProductPage(
                        usingProduct.id,
                        usingProduct.userId,
                        usingProduct.subscriptionDate,
                        usingProduct.type,
                        usingProduct.name,
                        usingProduct.productId
                ))
                .from(usingProduct)
                .where(builder);

        query.orderBy(usingProduct.createdAt.desc());

        List<UsingProductPage> content = query
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        return content;
    }

    private BooleanExpression userIdEq(UUID id){
        return id != null ? usingProduct.userId.eq(id) : null;
    }

    private BooleanExpression name(String name){
        return name != null ? usingProduct.name.like(name+"%") : null;
    }


}

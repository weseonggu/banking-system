package com.msa.banking.product.domain.repository;

import com.msa.banking.product.application.dto.QResponseProductPage;
import com.msa.banking.product.application.dto.ResponseProductPage;
import com.msa.banking.product.domain.ProductType;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.msa.banking.product.domain.model.QProduct.product;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ResponseProductPage> findAllProduct(Pageable pageable, RequestSearchProductDto condition) {

        Sort sort = pageable.getSort();

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(isDeletedEq(condition.getIs_deleted()))
                .and(typeEq(condition.getType()))
                .and(betweenDates(condition.getValidFrom(), condition.getValidTo()));

        JPAQuery<ResponseProductPage> query = queryFactory
                .select(new QResponseProductPage(
                        product.id,
                        product.name,
                        product.type
                ))
                .from(product)
                .where(builder);
        List<ResponseProductPage> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return content;
    }

    private BooleanExpression isDeletedEq(Boolean isPublic) {
        return isPublic != null ? product.isDelete.eq(isPublic) : null;
    }

    private BooleanExpression typeEq(ProductType type) {
        return type != null ? product.type.eq(type) : null;
    }

    private BooleanExpression betweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        // null 체크 후 between 조건 추가
        return (startDate != null && endDate != null)
                ? product.createdAt.between(startDate, endDate)
                : null;
    }
}

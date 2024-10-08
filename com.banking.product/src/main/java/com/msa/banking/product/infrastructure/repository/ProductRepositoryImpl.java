package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.presentation.response.QResponseProductPage;
import com.msa.banking.product.presentation.response.ResponseProductPage;
import com.msa.banking.product.lib.ProductType;
import com.msa.banking.product.domain.repository.ProductRepositoryCustom;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static com.msa.banking.product.domain.model.QProduct.product;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

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

        if (sort.isSorted()) {
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isDescending() ? product.createdAt.desc() : product.createdAt.asc());
                } else {
                    query.orderBy(direction.isDescending() ? product.createdAt.desc() : product.createdAt.asc());
                }
            });
        }
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

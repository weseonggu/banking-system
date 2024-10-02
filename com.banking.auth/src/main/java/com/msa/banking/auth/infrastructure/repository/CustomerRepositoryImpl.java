package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.repository.CustomerRepositoryCustom;
import com.msa.banking.auth.presentation.request.SearchRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.msa.banking.auth.domain.model.QCustomer.*;
import static org.springframework.util.StringUtils.*;


public class CustomerRepositoryImpl implements CustomerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CustomerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AuthResponseDto> findPagingAllCustomer(Pageable pageable, SearchRequestDto condition) {

        JPAQuery<Customer> query = queryFactory
                .selectFrom(customer)
                .where(customerIdEq(condition.getUserId()),
                        usernameEq(condition.getUsername()),
                        nameEq(condition.getName()),
                        emailEq(condition.getEmail()),
                        phoneNumberEq(condition.getPhoneNumber()),
                        cityContains(condition.getCity()),
                        streetContains(condition.getStreet()),
                        zipcodeContains(condition.getZipcode()));

        if (pageable.getSort().isSorted()) {
            query.orderBy(customer.createdAt.asc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? customer.createdAt.asc() : customer.createdAt.desc());
                }else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? customer.updatedAt.asc() : customer.updatedAt.desc());
                }
            });
        }

        List<Customer> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(customer.count())
                .from(customer)
                .where(customerIdEq(condition.getUserId()),
                        usernameEq(condition.getUsername()),
                        nameEq(condition.getName()),
                        emailEq(condition.getEmail()),
                        phoneNumberEq(condition.getPhoneNumber()),
                        cityContains(condition.getCity()),
                        streetContains(condition.getStreet()),
                        zipcodeContains(condition.getZipcode()));

        List<AuthResponseDto> dtoList = new ArrayList<>();
        for (Customer authResponseDto : content) {
            dtoList.add(AuthResponseDto.toDto(authResponseDto));
        }

        return PageableExecutionUtils.getPage(dtoList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression zipcodeContains(String zipcode) {
        return hasText(zipcode) ? customer.address.zipcode.contains(zipcode) : null;
    }

    private BooleanExpression streetContains(String street) {
        return hasText(street) ? customer.address.street.contains(street) : null;
    }

    private BooleanExpression cityContains(String city) {
        return hasText(city) ? customer.address.city.contains(city) : null;
    }

    private BooleanExpression phoneNumberEq(String phoneNumber) {
        return hasText(phoneNumber) ? customer.phoneNumber.eq(phoneNumber) : null;
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? customer.email.eq(email) : null;
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? customer.name.eq(name) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? customer.username.eq(username) : null;
    }

    private BooleanExpression customerIdEq(UUID userId) {
        return userId != null ? customer.id.eq(userId) : null;
    }
}

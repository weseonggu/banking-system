package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.auth.domain.repository.EmployeeRepositoryCustom;
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

import static com.msa.banking.auth.domain.model.QEmployee.employee;
import static org.springframework.util.StringUtils.hasText;

public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public EmployeeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AuthResponseDto> findPagingAllEmployee(Pageable pageable, SearchRequestDto condition) {

        JPAQuery<Employee> query = queryFactory
                .selectFrom(employee)
                .where(customerIdEq(condition.getUserId()),
                        usernameEq(condition.getUsername()),
                        nameEq(condition.getName()),
                        emailEq(condition.getEmail()),
                        phoneNumberEq(condition.getPhoneNumber()),
                        cityContains(condition.getCity()),
                        streetContains(condition.getStreet()),
                        zipcodeContains(condition.getZipcode()));

        if (pageable.getSort().isSorted()) {
            query.orderBy(employee.createdAt.asc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? employee.createdAt.asc() : employee.createdAt.desc());
                }else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? employee.updatedAt.asc() : employee.updatedAt.desc());
                }
            });
        }

        List<Employee> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(employee.count())
                .from(employee)
                .where(customerIdEq(condition.getUserId()),
                        usernameEq(condition.getUsername()),
                        nameEq(condition.getName()),
                        emailEq(condition.getEmail()),
                        phoneNumberEq(condition.getPhoneNumber()),
                        cityContains(condition.getCity()),
                        streetContains(condition.getStreet()),
                        zipcodeContains(condition.getZipcode()));

        List<AuthResponseDto> dtoList = new ArrayList<>();
        for (Employee authResponseDto : content) {
            dtoList.add(AuthResponseDto.toDto(authResponseDto));
        }

        return PageableExecutionUtils.getPage(dtoList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression zipcodeContains(String zipcode) {
        return hasText(zipcode) ? employee.address.zipcode.contains(zipcode) : null;
    }

    private BooleanExpression streetContains(String street) {
        return hasText(street) ? employee.address.street.contains(street) : null;
    }

    private BooleanExpression cityContains(String city) {
        return hasText(city) ? employee.address.city.contains(city) : null;
    }

    private BooleanExpression phoneNumberEq(String phoneNumber) {
        return hasText(phoneNumber) ? employee.phoneNumber.eq(phoneNumber) : null;
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? employee.email.eq(email) : null;
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? employee.name.eq(name) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? employee.username.eq(username) : null;
    }

    private BooleanExpression customerIdEq(UUID userId) {
        return userId != null ? employee.id.eq(userId) : null;
    }
}

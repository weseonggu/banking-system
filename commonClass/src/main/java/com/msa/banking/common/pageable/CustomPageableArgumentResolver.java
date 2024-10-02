package com.msa.banking.common.pageable;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


import java.util.List;

@Component
public class CustomPageableArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        int page = getPage(webRequest);
        int size = getSize(webRequest);
        Sort sort = getSort(webRequest);

        if (page < 0) {
            page = 0;
        }

        return PageRequest.of(page, size, sort);
    }
    private int getPage(NativeWebRequest webRequest) {
        String page = webRequest.getParameter("page");
        return (page != null) ? Integer.parseInt(page) - 1 : 0;
    }

    private int getSize(NativeWebRequest webRequest) {
        String size = webRequest.getParameter("size");
        return (size != null && List.of(10, 30, 50).contains(Integer.parseInt(size))) ? Integer.parseInt(size) : 10;
    }

    private Sort getSort(NativeWebRequest webRequest) {
        String sort = webRequest.getParameter("sort");
        String direction = webRequest.getParameter("direction");

        if (sort == null && direction == null) {
            return Sort.by("createdAt").descending();
        }

        if (sort == null && direction != null) {
            if (direction == "desc") {
                return Sort.by("createdAt").descending();
            }
            return  Sort.by("createdAt").ascending();
        }

        if (sort != null && direction == null) {
            return Sort.by(sort).ascending();
        }

        if (direction == "desc") {
            return Sort.by(sort).descending();
        }
        return Sort.by(sort).ascending();

    }
}

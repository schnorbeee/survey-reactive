package com.dynata.surveyhw.resolvers;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactivePageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String PAGE_PARAMETER = "page";
    private static final String SIZE_PARAMETER = "size";
    private static final String SORT_PARAMETER = "sort";

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter,
            BindingContext bindingContext,
            ServerWebExchange exchange) {

        var request = exchange.getRequest();
        var queryParams = request.getQueryParams();

        int page = queryParams.containsKey(PAGE_PARAMETER)
                ? Integer.parseInt(queryParams.getFirst(PAGE_PARAMETER)) - 1
                : DEFAULT_PAGE;

        int size = queryParams.containsKey(SIZE_PARAMETER)
                ? Integer.parseInt(queryParams.getFirst(SIZE_PARAMETER))
                : DEFAULT_SIZE;

        Sort sort = Sort.unsorted();
        if (queryParams.containsKey(SORT_PARAMETER)) {
            sort = Sort.by(queryParams.get(SORT_PARAMETER).stream()
                    .map(order -> {
                        String[] parts = order.split(",");
                        if (parts.length == 2) {
                            return new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]);
                        } else {
                            return new Sort.Order(Sort.Direction.ASC, parts[0]);
                        }
                    })
                    .toList());
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return Mono.just(pageable);
    }
}

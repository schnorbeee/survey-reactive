package com.dynata.surveyhw.configurations;

import com.dynata.surveyhw.resolvers.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new ReactivePageableHandlerMethodArgumentResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/openapi/examples/**")
                .addResourceLocations("classpath:/static/openapi/examples/");
    }
}


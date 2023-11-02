package com.sprintform.interview.budgetplanner.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@RequiredArgsConstructor
@Configuration
public class RequestLoggingFilterConfig {

    private final RequestLoggingProperties properties;

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(properties.getIncludeQueryString());
        filter.setIncludePayload(properties.getIncludePayload());
        filter.setIncludeHeaders(properties.getIncludeHeaders());
        filter.setMaxPayloadLength(properties.getMaxPayloadLength());
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}

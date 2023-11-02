package com.sprintform.interview.budgetplanner.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "logging.request")
public class RequestLoggingProperties {

    private Boolean includeQueryString = true;
    private Boolean includePayload = true;
    private Boolean includeHeaders = false;
    private Integer maxPayloadLength = 10000;
}

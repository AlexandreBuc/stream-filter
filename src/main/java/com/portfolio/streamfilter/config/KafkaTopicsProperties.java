package com.portfolio.streamfilter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("kafka.topic")
@ConstructorBinding
@Validated
@Data
public class KafkaTopicsProperties {
    @NotNull
    private final In in;
    @NotNull
    private final Out out;
    @NotNull
    private final StateStore stateStore;
    @NotBlank
    private final String dlq;

    @ConstructorBinding
    @Data
    public static class In {
        @NotBlank
        private final String carTopic;
        @NotBlank
        private final String countryTopic;
    }

    @ConstructorBinding
    @Data
    public static class Out {
        @NotBlank
        private final String resultTopic;
    }

    @ConstructorBinding
    @Data
    public static class StateStore {
        @NotBlank
        private final String countryStore;
    }
}

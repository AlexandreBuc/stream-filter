package com.portfolio.streamfilter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("kafka.topic")
@ConstructorBinding
@Validated
@Data
public class KafkaTopicsProperties {
    In in;
    Out out;
    String dlq;

    @ConstructorBinding
    @Data
    public static class In {
        String carTopic;
        String countyTopic;
    }

    @ConstructorBinding
    @Data
    public static class Out {
        String resultTopic;
    }
}

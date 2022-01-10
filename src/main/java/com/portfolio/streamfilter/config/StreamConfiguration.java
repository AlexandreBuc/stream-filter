package com.portfolio.streamfilter.config;

import com.portfolio.streamfilter.domain.Car;
import com.portfolio.streamfilter.domain.Country;
import com.portfolio.streamfilter.domain.JoinResult;
import com.portfolio.streamfilter.mapper.CarMapper;
import com.portfolio.streamfilter.utils.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.List;
import java.util.Map;


@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
public class StreamConfiguration {

    private final KafkaTopicsProperties topics;

    private static final Serde<String> stringSerde = Serdes.String();
    private final Serde<Car> carSerde = Serdes.serdeFrom(Car.class);
    private final Serde<Country> countrySerde = Serdes.serdeFrom(Country.class);
    private final Serde<JoinResult> joinResultSerde = Serdes.serdeFrom(JoinResult.class);

    @Bean
    public KStream<String, Car> stream(StreamsBuilder streamsBuilder) {

        /*Create country state store*/
        streamsBuilder
                .globalTable(topics.getIn().getCountryTopic(),
                        Materialized.<String, Country, KeyValueStore<Bytes, byte[]>>as(topics.getStateStore().getCountryStore())
                                .withKeySerde(stringSerde)
                                .withValueSerde(countrySerde));

        /*Get cars data*/
        final KStream<String, Car> carKStream = streamsBuilder.stream(topics.getIn().getCarTopic(), Consumed.with(stringSerde, carSerde));

        /*Join and branch cars data with countries*/
        final Map<String, KStream<String, JoinResult>> branches = carKStream
                .transformValues(() -> new Joiner(topics.getStateStore().getCountryStore()), Named.as("joining_car_and_countries"))
                .split(Named.as("branch_"))
                .branch((key, value) -> !filterConditions(value.getCountries()), Branched.as("KO"))
                .defaultBranch(Branched.as("OK"));

        /*Send error events to DLQ*/
        branches.get("branch_KO")
                .to(topics.getDlq(), Produced.with(stringSerde, joinResultSerde));

        /*Send cars data to out topic*/
        KStream<String, Car> outStream = branches.get("branch_OK")
                .mapValues(CarMapper::joinResultToCar);
        outStream
                .to(topics.getOut().getResultTopic(), Produced.with(stringSerde, carSerde));

        log.info("\n{}\n", streamsBuilder.build().describe());

        return outStream;
    }

    private boolean filterConditions(List<Country> countries) {
        return (countries != null) && !countries.isEmpty();
    }

}

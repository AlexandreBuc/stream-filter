package com.portfolio.streamfilter.utils;

import com.portfolio.streamfilter.domain.Car;
import com.portfolio.streamfilter.domain.Country;
import com.portfolio.streamfilter.domain.JoinResult;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class Joiner implements ValueTransformer<Car, JoinResult> {

    private final String storeName;

    private KeyValueStore<String, Country> globalCountryStore;


    @Override
    public void init(ProcessorContext processorContext) {
        globalCountryStore = (KeyValueStore<String, Country>) processorContext.getStateStore(storeName);
    }

    @Override
    public JoinResult transform(Car car) {
        List<Country> countryList = new ArrayList<>();

        for(String countryName: car.getApprovedCountries()){
            Optional<Country> countryOptional = Optional.ofNullable(globalCountryStore.get(countryName));
            countryOptional.ifPresent(countryList::add);
        }

        return JoinResult.builder()
                .car(car)
                .countries(countryList)
                .build();
    }

    @Override
    public void close() {
        // TODO document why this method is empty
    }
}

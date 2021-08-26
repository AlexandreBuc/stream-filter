package com.portfolio.streamfilter.domain;

import lombok.Value;

import java.util.List;

@Value
public class Car {

    String brand;
    String model;
    String owner;
    String fuel;
    List<String> approvedCountries;
}

package com.portfolio.streamfilter.domain;

import lombok.Value;

import java.util.List;

@Value
public class JoinResult {
    Car car;
    List<Country> countries;
}

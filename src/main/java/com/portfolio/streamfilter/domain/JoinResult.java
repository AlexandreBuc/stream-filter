package com.portfolio.streamfilter.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class JoinResult {
    Car car;
    List<Country> countries;
}

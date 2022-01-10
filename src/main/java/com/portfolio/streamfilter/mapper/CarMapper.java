package com.portfolio.streamfilter.mapper;

import com.portfolio.streamfilter.domain.Car;
import com.portfolio.streamfilter.domain.JoinResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CarMapper {

    public static Car joinResultToCar(JoinResult joinResult) {
        return joinResult.getCar();
    }

}

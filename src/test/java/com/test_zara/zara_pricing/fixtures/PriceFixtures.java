package com.test_zara.zara_pricing.fixtures;

import com.test_zara.zara_pricing.domain.model.Price;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class PriceFixtures {

    public List<Price> getPricesFixtures() {
        return List.of(
                new Price(
                    1,
                    LocalDateTime.of(2020, 6, 14, 0, 0),
                    LocalDateTime.of(2020, 12, 31, 23, 59),
                    2,
                    35455L,
                    0,
                    new BigDecimal("19.99"),
                    "EUR"
                ),
                new Price(
                    2,
                    LocalDateTime.of(2021, 1, 1, 0, 0),
                    LocalDateTime.of(2021, 12, 31, 23, 59),
                    3,
                    12345L,
                    1,
                    new BigDecimal("29.99"),
                    "EUR"
                )
        );
    }

    public LocalDateTime now = LocalDateTime.now();
}

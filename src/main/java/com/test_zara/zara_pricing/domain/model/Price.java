package com.test_zara.zara_pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Price(
        Integer brandId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer priceList,
        Long productId,
        Integer priority,
        BigDecimal price,
        String currency
) {}

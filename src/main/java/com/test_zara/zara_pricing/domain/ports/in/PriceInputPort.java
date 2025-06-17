package com.test_zara.zara_pricing.domain.ports.in;


import com.test_zara.zara_pricing.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceInputPort {

    Price getFinalPrice(LocalDateTime date, Long productId, Integer brandId);

    Price getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId);

    List<Price> getPricesListAll();
}
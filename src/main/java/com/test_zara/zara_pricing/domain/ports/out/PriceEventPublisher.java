package com.test_zara.zara_pricing.domain.ports.out;

import com.test_zara.zara_pricing.domain.model.Price;

public interface PriceEventPublisher {
    void publishPriceRetrieved(Price price);
} 
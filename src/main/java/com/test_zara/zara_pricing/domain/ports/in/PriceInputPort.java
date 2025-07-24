package com.test_zara.zara_pricing.domain.ports.in;


import com.test_zara.zara_pricing.domain.model.Price;

import java.time.LocalDateTime;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface PriceInputPort {

    Mono<Price> getFinalPrice(LocalDateTime date, Long productId, Integer brandId);

    Mono<Price> getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId);

    Flux<Price> getPricesListAll();
}
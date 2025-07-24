package com.test_zara.zara_pricing.domain.ports.out;

import com.test_zara.zara_pricing.domain.model.Price;
import java.time.LocalDateTime;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface PriceOutputPort {

    Mono<Price> findFinalPrice(
            Integer brandId,
            Long productId,
            LocalDateTime date
    );

    Flux<Price> findFinalPriceWithConvention(
            Integer brandId,
            Long productId,
            LocalDateTime date
    );

    Flux<Price> findAll();
}

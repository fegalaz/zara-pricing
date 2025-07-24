package com.test_zara.zara_pricing.application.services;


import com.test_zara.zara_pricing.domain.model.Price;

import java.time.LocalDateTime;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface PriceService {

    // Ahora el método devuelve un Mono (reactivo) en vez de Price
    Mono<Price> getFinalPrice(LocalDateTime date, Long productId, Integer brandId);

    // También migramos este método a Mono
    Mono<Price> getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId);

    // Este método ahora devuelve un Flux (muchos elementos)
    Flux<Price> getPricesListAll();
}

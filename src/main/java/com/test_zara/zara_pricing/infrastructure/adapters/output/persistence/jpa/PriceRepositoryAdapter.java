package com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa;

import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.ports.out.PriceOutputPort;
import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.repository.DataPriceRepository;
import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements PriceOutputPort {

    private final DataPriceRepository jpaRepository;
    private final PriceMapper priceMapper;

    @Override
    public Mono<Price> findFinalPrice(
            Integer brandId,
            Long productId,
            LocalDateTime date
    ) {
        return jpaRepository
                .findFinalPriceWithJpql(brandId, productId, date)
                .map(priceMapper::toDomain);
    }

    @Override
    public Flux<Price> findFinalPriceWithConvention(
            Integer brandId,
            Long productId,
            LocalDateTime date
    ) {
        return jpaRepository
                .findFinalPriceWithConvention(brandId, productId, date)
                .map(priceMapper::toDomain);
    }

    @Override
    public Flux<Price> findAll() {
        return jpaRepository.findAll().map(priceMapper::toDomain);
    }
}
